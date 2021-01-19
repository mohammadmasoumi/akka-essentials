package part4faulttolerance

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorSystem, OneForOneStrategy, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SuperVisionSpec extends TestKit(ActorSystem("SupervisionSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  // import companion object

}

object SuperVisionSpec {

  class Supervisor extends Actor {

    /**
     * takes a partial function from a throwable to a strategy
     */
    override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy() {
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop

    }

    override def receive: Receive = {
      case props: Props =>
        val childRef = context.actorOf(props)
        sender() ! childRef
    }
  }

  class FussyWordCounter extends Actor {

    case object Report

    override def receive: Receive = wordCounterHandler()

    def wordCounterHandler(words: Int = 0): Receive = {
      case Report =>
        sender() ! words
      case "" => throw new NullPointerException("sentence is empty")
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
