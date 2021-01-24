package part5infra

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.util.Random

object Dispatchers extends App {

  class Counter extends Actor with ActorLogging {
    override def receive: Receive = onMessage()

    private def onMessage(count: Int = 0): Receive = {
      case message =>
        context.become(onMessage(count + 1))
        log.info(s"[$count]: $message")
    }
  }

  val system = ActorSystem("DispatcherDemo") // , ConfigFactory.load().getConfig("dispatcherDemo")

  /**
   * METHOD #1 - programmatic / in code
   *
   * change `fixed-pool-size` to 1 to see what will happen.
   */

  val actors: Seq[ActorRef] = for (idx <- 1 to 10)
    yield system.actorOf(Props[Counter].withDispatcher("my-dispatcher"), s"counter_$idx")

  // test the actors
  val r = new Random()
  for (idx <- 1 to 1000) {
    actors(r.nextInt(10)) ! idx
  }

  /**
   * METHOD #2 - from config
   */
  val rtjvm = system.actorOf(Props[Counter], "rtjvm")

  /**
   * Dispatchers implement the ExecutionContext trait
   */


}

