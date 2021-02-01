package part6patterns

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

// the first step
import akka.pattern.ask

class AskSpec extends TestKit(ActorSystem("AskSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import AskSpec._

  "An authenticator" should {

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

  object AuthManager {
    val AUTH_FAILURE_NOT_FOUND = "username not found"
    val AUTH_FAILURE_PASSWORD_INCORRECT = "password incorrect"
    val AUTH_FAILURE_SYSTEM = "system error"
  }

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
        val originalSender = sender()
        // step 3 - ask the actor - futures run in separate thread
        val future = authDB ? Read(username)
        // step 4 - handle the future for e.g. with onComplete
        future.onComplete {
          // step 5 the most important
          // NEVER CALL METHODS ON THE ACTOR INSTANCE OR ACCESS MUTABLE STATE IN ON_COMPLETE
          // Goal: avoid closing over the actor instance or mutable state
          case Success(None) =>
            originalSender ! AuthFailure(AuthManager.AUTH_FAILURE_NOT_FOUND)
          case Success(dbPassword) =>
            if (dbPassword == password)  originalSender ! AuthSuccess
            else originalSender ! AuthFailure(AuthManager.AUTH_FAILURE_PASSWORD_INCORRECT)
          case Failure(_) => originalSender ! AuthFailure(AuthManager.AUTH_FAILURE_SYSTEM)
        }
    }
  }

}