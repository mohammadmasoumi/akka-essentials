package part3testing

import akka.actor.Actor
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SynchronousTestingSpec extends WordSpecLike with BeforeAndAfterAll {

}

object SynchronousTestingSpec {

  class Counter extends Actor {
    override def receive: Receive = ???
  }

}