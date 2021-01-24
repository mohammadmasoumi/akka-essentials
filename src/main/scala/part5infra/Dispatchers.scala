package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.concurrent.{ExecutionContext, Future}
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

  //  val actors: Seq[ActorRef] = for (idx <- 1 to 10)
  //    yield system.actorOf(Props[Counter].withDispatcher("my-dispatcher"), s"counter_$idx")

  // test the actors
  val r = new Random()

  //  for (idx <- 1 to 1000) {
  //    actors(r.nextInt(10)) ! idx
  //  }

  /**
   * METHOD #2 - from config
   */
  //  val rtjvm = system.actorOf(Props[Counter], "rtjvm")

  /**
   * Dispatchers implement the ExecutionContext trait
   * Interact with IO
   */

  class DBActor extends Actor with ActorLogging {
    // solution #1 - dispatcher
    // you can use `contest.dispatcher` or a dedicated one!
    // implicit val executionContext: ExecutionContext = context.dispatcher
    implicit val executionContext: ExecutionContext = context.system.dispatchers.lookup("my-dispatcher")
    // solution #2 - a Router

    // using Future in Actors are discouraged!
    override def receive: Receive = {
      case message => Future {
        // wait on a resource
        Thread.sleep(5000)
        log.info(s"Success: $message")
      }
    }
  }

  val dbActor = system.actorOf(Props[DBActor])
  //  dbActor ! "The meaning of the life is 24"

  // a nonblocking actor
  val nonblockingActor = system.actorOf(Props[Counter])

  for (idx <- 1 to 1000) {
    val message = s"important message $idx"
    dbActor ! message
    nonblockingActor ! message
  }


}

