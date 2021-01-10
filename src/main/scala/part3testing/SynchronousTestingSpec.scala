package part3testing

import akka.actor.{Actor, ActorLogging}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SynchronousTestingSpec extends WordSpecLike with BeforeAndAfterAll {

}

object SynchronousTestingSpec {

  case object Inc

  case object Read

  class Counter extends Actor with ActorLogging {
    override def receive: Receive = counterHandler()

    def counterHandler(count: Int = 0): Receive = {
      case Inc =>
        val newCount = count + 1
        log.info(s"Incremented count is: $count")
        context.become(counterHandler(newCount))

      case Read =>
        log.info(s"Reading count: $count")
        sender() ! count
    }
  }

}