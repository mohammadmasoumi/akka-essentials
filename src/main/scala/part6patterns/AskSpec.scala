package part6patterns

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Success

// the first step
import akka.pattern.ask

class AskSpec extends TestKit(ActorSystem("AskSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}


object AskSpec {


  // this code is somewhere else in your application
  case class Read(key: String)

  case class Write(key: String, value: String)

  class KVActor extends Actor with ActorLogging {

    override def receive: Receive = online(Map())

    def online(kv: Map[String, String]): Receive = {
      case Read(key: String) =>
        log.info(s"Trying to read the value of the key $key")
        sender() ! kv.get(key) // Option[String]

      case Write(key: String, value: String) =>
        log.info(s"Trying to write value $value for key $key")
        context.become(online(kv + (key -> value)))
    }
  }

  // user authenticator actor
  case class RegisterUser(username: String, password: String)

  case class Authenticate(username: String, password: String)

  case class AuthFailure(message: String)

  case object AuthSuccess

  class AuthManager extends Actor with ActorLogging {
    // step 2 = logistic
    implicit val timeout: Timeout = Timeout(1 second)
    implicit val executionContext: ExecutionContext = context.dispatcher

    private val authDB = context.actorOf(Props[KVActor])

    /**
     * with future approach; we break the actor encapsulation.
     * who would be the sender in the future on_complete method?
     * @return
     */
    override def receive: Receive = {
      case RegisterUser(username, password) =>
        authDB ! Write(username, password)
      case Authenticate(username, password) =>
        // step 3 - ask the actor - futures run in separate thread
        val future = authDB ? Read(username)
        // step 4 - handle the future for e.g. with onComplete
        future.onComplete {
          // step 5 the most important
          // NEVER CALL METHODS ON THE ACTOR INSTANCE OR ACCESS MUTABLE STATE IN ON_COMPLETE
          case Success(None) =>
            sender() ! AuthFailure("username not found!")
          case Success(dbPassword) =>
            if (dbPassword == password)  sender() ! AuthSuccess
            else sender() ! AuthFailure("password incorrect")
        }
    }
  }

}