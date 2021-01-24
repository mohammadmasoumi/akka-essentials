package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Dispatchers extends App {

  class Counter extends Actor with ActorLogging {
    override def receive: Receive = onMessage()

    private def onMessage(count: Int = 0): Receive = {
      case message =>
        context.become(onMessage(count + 1))
        log.info(s"[$count]: $message")
    }
  }

  val system = ActorSystem("DispatcherDemo", ConfigFactory.load().getConfig("dispatcherDemo"))

  val actors = for (idx <- 1 to 10)
    yield system.actorOf(Props[Counter].withDispatcher("my-dispatcher"), s"actor_$idx")

}

