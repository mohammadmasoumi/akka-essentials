package part4faulttolerance

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, AllForOneStrategy, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._

class SuperVisionSpec extends TestKit(ActorSystem("SupervisionSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  // import companion object

  import SuperVisionSpec._

  "A supervisor" should {
    "resume its child in case of a minor fault" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Report
      expectMsg(3)

      /**
       * In resume strategy the internal state of an Actor wouldn't change.
       */
      child ! "Akka is awesome because I am learning to think in a whole new way"
      child ! Report
      expectMsg(3)
    }

    "restart its child in case of an empty sentence" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "I love Akka"
      child ! Report
      expectMsg(3)

      /**
       * In restart strategy the internal state of an Actor would be destroyed.
       */
      child ! ""
      child ! Report
      expectMsg(0)
    }

    "terminate its child in case of a major error" in {
      val supervisor = system.actorOf(Props[Supervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      /**
       * In stop strategy the actor should be terminated!.
       */
      watch(child)
      child ! "akka is nice"
      val terminatedMessage = expectMsgType[Terminated]
      assert(terminatedMessage.actor == child)
    }

    "escalate an error when it doesn't know what to do" in {
      val supervisor = system.actorOf(Props[Supervisor], "supervisor")
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      /**
       * In escalate strategy.
       * 1- stops all its children
       * 2- scalate the error to its the parent
       */
      watch(child)
      child ! 1
      val terminatedMessage = expectMsgType[Terminated]
      assert(terminatedMessage.actor == child)
    }
  }

  "A kinder supervisor" should {
    "not kill children in case it's restarted or escalated failures" in {
      val supervisor = system.actorOf(Props[NoDeathOnRestartSupervisor])
      supervisor ! Props[FussyWordCounter]
      val child = expectMsgType[ActorRef]

      child ! "Akka is cool"
      child ! Report
      expectMsg(3)

      /**
       * child is still alive
       * but the user guardian restarted everything.
       */
      child ! 1
      child ! Report
      expectMsg(0)
    }

    "An all-for-one supervisor" should {
      "apply the all-for-one strategy" in {
        val supervisor = system.actorOf(Props[AllForOneSupervisor], "allForOneSupervisor")
        supervisor ! Props[FussyWordCounter]
        val firstChild = expectMsgType[ActorRef]

        supervisor ! Props[FussyWordCounter]
        val secondChild = expectMsgType[ActorRef]

        firstChild ! "Testing supervision"
        firstChild ! Report
        expectMsg(2)

        // cause the first child to be failed
        EventFilter[NullPointerException]() intercept {
          firstChild ! ""
        }

        Thread.sleep(500)
        // all children has been restarted by all-for-one strategy!
        secondChild ! Report
        expectMsg(0)
      }
    }

  }
}

object SuperVisionSpec {

  class Supervisor extends Actor with ActorLogging {

    /**
     * takes a partial function from a throwable to a strategy
     *
     * maxNrOfRetries, withinTimeRange => retry strategies
     */
    override val supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException => Resume
      case _: Exception => Escalate // increase rapidly.
    }

    override def receive: Receive = {
      case props: Props =>
        val childRef = context.actorOf(props)
        sender() ! childRef
    }
  }

  class NoDeathOnRestartSupervisor extends Supervisor {
    override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
      // this is empty
    }
  }

  class AllForOneSupervisor extends Supervisor {
    /**
     * takes a partial function from a throwable to a strategy
     *
     * if one of the children cause the failure all children will fail with the same strategy!
     */
    override val supervisorStrategy: SupervisorStrategy = AllForOneStrategy() {
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException => Resume
      case _: Exception => Escalate // increase rapidly.
    }
  }

  case object Report

  class FussyWordCounter extends Actor with ActorLogging {
    override def receive: Receive = wordCounterHandler()

    def wordCounterHandler(words: Int = 0): Receive = {
      case Report =>
        sender() ! words
      case "" =>
        throw new NullPointerException("sentence is empty")
      case sentence: String =>
        if (sentence.length > 20)
          throw new RuntimeException("sentence is too big")
        else if (!Character.isUpperCase(sentence.charAt(0)))
          throw new IllegalArgumentException("sentence must start with uppercase")
        else context.become(wordCounterHandler(words + sentence.split(" ").length))
      case _ =>
        throw new Exception("can only receive strings")
    }
  }

}
