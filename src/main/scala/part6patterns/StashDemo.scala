package part6patterns

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}


object StashDemo extends App {

  /**
   * ResourceActor
   * - Open => it can receivve read/write requests to the resource.
   * - otherwise => it will postpone all read/write requests until the state is open.
   *
   * ResourceActor is closed.
   * - Open => it will switch to the open state.
   * - Read, Write messages are POSTPONED.
   *
   * ResourceActor is open.
   * - Read/Write are handled.
   * - Close => switch to the closed state.
   *
   * [Open, Read, Read, Write]
   * - swtich to the Open state.
   * - read the data
   * - read the data
   * - write the data
   *
   */

  case object Open

  case object Close

  case object Read

  case class Write(data: String)

  // step 1 - mix-in the Stash trait
  class ResourceActor extends Actor with ActorLogging with Stash {
    private var innerData: String = ""

    override def receive: Receive = closed

    def closed: Receive = {
      case Open =>
        log.info("Opening resource")
        // step3 - unStashAll when you switch the message handler.
        unstashAll()
        context.become(open)

      case message =>
        log.info(s"Stashing $message because I can't handle it in the closed state.")
        // step2 - stash away what you can't handle
        stash()
    }

    def open: Receive = {
      case Read =>
        // do some actual computation.
        log.info(s"I have read $innerData")

      case Write(data) =>
        log.info(s"I am writing $data")
        innerData = data
      case Close =>
        log.info("Closing resource.")
        unstashAll()
        context.become(closed)
      case message =>
        log.info(s"Stashing $message because I can't handle it in the open state.")
        stash()
    }
  }

  val system  = ActorSystem("StashDemo")
  val resourceActor = system.actorOf(Props[ResourceActor])


}