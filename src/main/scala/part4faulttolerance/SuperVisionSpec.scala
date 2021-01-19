package part4faulttolerance

import akka.actor.{Actor, ActorSystem}
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
  import SuperVisionSpec._

}

object SuperVisionSpec {

  class FussyWordCounter extends Actor {

    override def receive: Receive = wordCounterHandler()

    def wordCounterHandler(words: Int =0): Receive = {
      case "" => throw new NullPointerException("sentence is empty")
      case sentence: String =>
        if (sentence.length > 20)
          throw new RuntimeException("sentence is too big")

    }
  }

}
