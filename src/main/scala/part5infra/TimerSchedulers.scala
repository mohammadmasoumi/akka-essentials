package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props, Timers}

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

  //  system.scheduler.scheduleOnce(1 seconds) {
  //    simpleActor ! "reminder"
  //  }

  // scheduling a repeated message
  /**
   * initialDelay: the amount of time that a scheduler should wait before a task has been initialized
   * interval: the interval between each tasks to be processed
   */
  //  val routine: Cancellable = system.scheduler.schedule(1 seconds, 2 seconds) {
  //    simpleActor ! "heartbeat"
  //  }

  // Cancellable is an object which can be cancelled
  //  system.scheduler.scheduleOnce(5 seconds) {
  //    system.log.info("Cancel routine task!")
  //    routine.cancel()
  //  }

  /**
   * Exercise: implement a self-closing actor
   * 1) if an actor receive a message (anything), you have 1 second to send it another message.
   * 2) if the time window expires, the actor will stop itself.
   * 3) if you send another message, the time window is reset.
   */

  class SelfClosingActor extends Actor with ActorLogging {

    import SelfClosingActor._

    def createTimeoutWindow(): Cancellable = {
      context.system.scheduler.scheduleOnce(MIN_WINDOW_INTERVAL) {
        self ! TimeoutMessage
      }
    }

    override def receive: Receive = closeHandler()

    def closeHandler(schedule: Cancellable = createTimeoutWindow()): Receive = {
      case TimeoutMessage =>
        log.info("stopping myself")
        context.stop(self)
      case message =>
        log.info(s"received $message staying alive!")
        schedule.cancel()
        context.become(closeHandler(createTimeoutWindow()))
    }
  }

  object SelfClosingActor {
    private val MIN_WINDOW_INTERVAL = 1 seconds

    case object TimeoutMessage

  }

  val selfClosingActor = system.actorOf(Props[SelfClosingActor], "selfClosingActor")

  val closingRoutine: Cancellable = system.scheduler.schedule(0.2 seconds, 0.5 seconds) {
    selfClosingActor ! "Akka is Awesome!"
  }
  system.scheduler.scheduleOnce(6 seconds) {
    closingRoutine.cancel()
  }

  /**
   * Timer: an Akka utility by which you can send message to yourself!
   */

  case object TimerKey

  case object Reminder

  case object Stop

  case object Start

  class TimerBasedHeartbeatActor extends Actor with ActorLogging with Timers {
    // ONLY One timer per timerKey
    // The message that I'm going to send to myself
    timers.startSingleTimer(TimerKey, Start, 500 millis)

    override def receive: Receive = {
      case Start =>
        log.info("Bootstrapping ...")
        // the previous timer cancelled
        timers.startPeriodicTimer(TimerKey, Reminder, 1 seconds)
      case Reminder =>
        log.info("I am Alive!")
      case Stop =>
        log.warning("Stopping ...")
        timers.cancel(TimerKey)
        context.stop(self)
    }
  }

}
