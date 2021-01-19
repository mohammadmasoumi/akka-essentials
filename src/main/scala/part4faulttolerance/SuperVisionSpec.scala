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

    }
  }

}
