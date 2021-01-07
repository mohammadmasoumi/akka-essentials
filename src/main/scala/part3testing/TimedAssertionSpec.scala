package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.util.Random

class TimedAssertionSpec extends TestKit(
  ActorSystem("TimedAssertionSpec", ConfigFactory.load().getConfig("specialTimedAssertionConfig")))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TimedAssertionSpec._

  "A worker actor" must {
    val workerActor = system.actorOf(Props[WorkerActor])

    "reply with meaning of the life in a timely manner" in {
      // at least 500 milli seconds, at most 1 second
      within(500 millis, 1 second) {
        // a time result box test
        workerActor ! "work"
        expectMsg(WorkResult(42))
      }
    }

    "reply with valid work at a reasonable cadence" in {
      within(1 second) {
        workerActor ! "workSequence"
        val results: Seq[Int] = receiveWhile[Int](max = 2 second, idle = 500 millis, messages = 10) {
          case WorkResult(result) => result
        }
        assert(results.sum > 5)
      }
    }

    "reply to a test probe in a timely manner" in {
      //this block will fail even though with 1 second timeout.
      // Check application.conf
      within(1 second) {
        val probe = TestProbe()
        probe.send(workerActor, "work")
        probe.expectMsg(WorkResult(42)) // timeout of 0.3 seconds
      }
    }


  }
}

object TimedAssertionSpec {

  case class WorkResult(result: Int)

  // testing scenario
  class WorkerActor extends Actor {
    override def receive: Receive = {
      case "work" =>
        // long computation
        Thread.sleep(500)
        sender() ! WorkResult(42)

      case "workSequence" =>
        val random = new Random()
        for (_ <- 1 to 10) {
          Thread.sleep(random.nextInt(50))
          sender() ! WorkResult(1)
        }
    }
  }

}
