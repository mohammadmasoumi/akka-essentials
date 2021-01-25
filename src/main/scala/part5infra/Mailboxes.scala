package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem}

object Mailboxes extends App {

  val system = ActorSystem("MailboxDemo")

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message =>
        log.info(message.toString)
    }
  }

  /**
   *
   *
   * Interesting case &1 - custom priority mailbox
   * P0 -> most important one
   * P1
   * P2
   * P3
   */





}
