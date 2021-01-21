package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props}

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

  // scheduling a repeated message
  /**
   * initialDelay: the amount of time that a scheduler should wait before a task has been initialized
   * interval: the interval between each tasks to be processed
   */
  val routine: Cancellable = system.scheduler.schedule(1 seconds, 2 seconds) {
    simpleActor ! "heartbeat"
  }

  // Cancellable is an object which can be cancelled
  system.scheduler.scheduleOnce(5 seconds) {
    system.log.info("Cancel routine task!")
    routine.cancel()
  }

  /**
   * Exercise: implement a self-closing actor
   *  - if an actor receive a message (anything), you have 1 second to send it another message.
   *  - if the time window expires, the actor will stop itself.
   *  - if you send another message, the time window is reset.
   */




}
