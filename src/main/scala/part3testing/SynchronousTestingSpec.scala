package part3testing

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.TestActorRef
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class SynchronousTestingSpec extends WordSpecLike with BeforeAndAfterAll {

  implicit val system = ActorSystem("SynchronousTestingSpec")

  override def afterAll(): Unit = {
    system.terminate()
  }

  import SynchronousTestingSpec._

  "A counter" should {

    "synchronously increase its counter" in {
      val counter = TestActorRef[Counter](Props[Counter])
      counter ! Inc // counter has already received the message
    }
    "synchronously increase its counter at the call of the receive function" in {
      val counter = TestActorRef[Counter](Props[Counter])
      counter.receive(Inc)
    }

  }

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