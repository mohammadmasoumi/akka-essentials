package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem}

object TimerSchedulers extends App {

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("SchedulersTimerDemo")

}
