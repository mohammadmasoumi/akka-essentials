package part3testing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class TestProbeSpec extends TestKit(ActorSystem("TestProbeSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TestProbeSpec._

  "A master actor" should {
    "register a slave" in {
      // master actor is stateful
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegistrationAck)
    }

    "send the word to the slave actor" in {
      // master actor is stateful
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegistrationAck)

      val workLoadString = "I love Akka"
      val workLoadLength = workLoadString.split(" ").length
      master ! Work(workLoadString)

      // the interaction between the master and the slave actor
      slave.expectMsg(SlaveWork(workLoadString, testActor))
      slave.reply(WorkCompleted(workLoadLength, testActor))
      expectMsg(Report(workLoadLength)) // testActor receives the Report(3)
    }
    "aggregation data correctly" in {
      // master actor is stateful
      val master = system.actorOf(Props[Master])
      val slave = TestProbe("slave")

      master ! Register(slave.ref)
      expectMsg(RegistrationAck)

      val workLoadString = "I love Akka"
      val workLoadLength = workLoadString.split(" ").length
      master ! Work(workLoadString)
      master ! Work(workLoadString)

      // in the meantime I don't have a slave actor
      slave.receiveWhile() {
        // this is also an assertion
        case SlaveWork(`workLoadString`, `testActor`) => slave.reply(WorkCompleted(workLoadLength, testActor))
      }

      expectMsg(Report(workLoadLength))
      expectMsg(Report(2 * workLoadLength))
    }

  }

}

object TestProbeSpec {

  // scenarios
  /*
    word counting actor hierarchy master-slave

    send some word to the master
      - master send the slave a piece of work
      - slave process the work and reply the master
      - master aggregates the result
    master sends the total count to to the original requester
   */
  case class Register(slaveRef: ActorRef)

  case class SlaveWork(test: String, originalRequester: ActorRef)

  case class WorkCompleted(count: Int, originalRequester: ActorRef)

  case class Work(test: String)

  case class Report(totalCount: Int)

  case object RegistrationAck

  class Master extends Actor {

    override def receive: Receive = {
      case Register(slaveRef) =>
        sender() ! RegistrationAck
        context.become(online(slaveRef, 0))
      case _ => // ignore
    }

    def online(slaveRef: ActorRef, totalWordCount: Int): Receive = {
      case Work(text) => slaveRef ! SlaveWork(text, sender())
      case WorkCompleted(count, originalRequester) =>
        val newTotalWordCount = totalWordCount + count
        originalRequester ! Report(newTotalWordCount)
        context.become(online(slaveRef, newTotalWordCount))
    }
  }

  // class Slave extends Actor ....

}
