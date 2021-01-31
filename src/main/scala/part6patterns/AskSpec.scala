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
  class KVActor extends Actor with ActorLogging {
    override def receive: Receive = ???

    def online(kv: Map[String, String]): Receive = {

    }
  }

}