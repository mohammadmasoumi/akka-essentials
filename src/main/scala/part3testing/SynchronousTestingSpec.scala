package part3testing

import akka.actor.Actor
import akka.io.Inet.SO.ReuseAddress
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SynchronousTestingSpec extends WordSpecLike with BeforeAndAfterAll {

}

object SynchronousTestingSpec {
  case object Inc
  case object Read

  class Counter extends Actor {
    override def receive: Receive = {

    }
  }

}