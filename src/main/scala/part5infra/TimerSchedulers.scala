package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.concurrent.duration._

object TimerSchedulers extends App {

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("SchedulersTimerDemo")
  val simpleActor = system.actorOf(Props[SimpleActor])

  system.log.info("scheduling reminder for simpleActor")

  // another solution for system.dispatcher
  // implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  // or

  import system.dispatcher

  system.scheduler.scheduleOnce(1 seconds) {
    simpleActor ! "reminder"
  }

}
