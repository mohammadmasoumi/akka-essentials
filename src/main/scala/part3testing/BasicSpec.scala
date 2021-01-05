package part3testing

import akka.actor.{Actor, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

// BEST PATTERN: end with `Spec` key
class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  // setup - tear down
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system) // system is a member of test kit.
  }
  "A simple actor" should { // test suit
    "do this" in { // testcase-1
      // testing scenario
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