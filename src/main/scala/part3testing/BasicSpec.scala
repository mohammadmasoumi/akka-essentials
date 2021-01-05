package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import part3testing.BasicSpec.SimpleActor

// BEST PATTERN: end with `Spec` key
class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  // setup - tear down
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system) // system is a member of test kit.
  }
  import BasicSpec._

  // test suit
  "A simple actor" should {

    // testcase-1
    "do this" in {
      // testing scenario
      val echoActor = system.actorOf(Props[SimpleActor])
      val message = "hello test"
      echoActor ! message

      expectMsg(message)
    }
  }
}

object BasicSpec {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message => sender() ! message
    }
  }
}