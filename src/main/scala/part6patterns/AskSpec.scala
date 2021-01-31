package part6patterns

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

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
        sender() ! kv.get(key)

      case Write(key: String, value: String) =>
        log.info(s"Trying to write value $value for key $key")
        sender() ! kv + (key -> value)
    }
  }

}