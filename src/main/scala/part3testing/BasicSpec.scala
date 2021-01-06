package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Random

// BEST PATTERN: end with `Spec` key
class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  /**
   * Q: Who is the sender?
   * A: testActor - that's what the ImplicitSender does
   *
   */

  // setup - tear down
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system) // system is a member of test kit.
  }

  // test section

  import BasicSpec._

  // test suit
  "A simple actor" should {
    // testcase-1
    "send back the same message" in {
      // testing scenario
      val echoActor = system.actorOf(Props[SimpleActor])
      val message = "hello test"
      echoActor ! message
      expectMsg(message) // akka.test.single-expect-default
    }
  }

  // test suit
  "A blackhole actor" should {
    // testcase-1
    "send back some message" in {
      // testing scenario
      val blackholeActor = system.actorOf(Props[BlackHole])
      val message = "hello test"
      blackholeActor ! message
      expectNoMessage(1 second)
    }
  }

  // message assertion
  "A lab test actor" should {
    // if you have stateful actor, it's recommended to create your actor inside the test-case
    val labTestActor = system.actorOf(Props[LabTestActor])

    "turn a string into uppercase" in {
      labTestActor ! "I love Akka"

      /**
       * expectMsg("I LOVE AKKA")
       * Or
       * val reply = expectMsgType[String]
       * assert(reply == "I LOVE AKKA")
       */

      // expectMsg("I LOVE AKKA")
      // Or
      val reply = expectMsgType[String]
      assert(reply == "I LOVE AKKA")
    }

    "reply to a greeting" in {
      labTestActor ! "greeting"
      expectMsgAnyOf("hi", "hello")
    }

    "reply with favorite tech" in {
      labTestActor ! "favoriteTech"
      expectMsgAllOf("Scala", "Akka")
    }

    "reply with coll tech in a different way" in {

    }
  }


}

object BasicSpec {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message => sender() ! message
    }
  }

  class BlackHole extends Actor {
    override def receive: Receive = Actor.emptyBehavior
  }

  class LabTestActor extends Actor {
    val random = new Random()

    override def receive: Receive = {
      case "greeting" =>
        if (random.nextBoolean()) sender() ! "hi" else sender() ! "hello"
      case "favoriteTech" =>
        sender() ! "Scala"
        sender() ! "Akka"
      case message: String => sender() ! message.toUpperCase()
    }
  }

}