package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

object Mailboxes extends App {

  val system = ActorSystem("MailboxDemo")

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message =>
        log.info(message.toString)
    }
  }

  /**
   * Interesting case &1 - custom priority mailbox
   * P0 -> most important one
   * P1
   * P2
   * P3
   */

  class SupportTicketPriorityMailbox(settings: ActorSystem.Settings, config: Config)
    extends UnboundedPriorityMailbox(
      PriorityGenerator {
        case message: String if message.startsWith("[P0]") => 0 // higher priority
        case message: String if message.startsWith("[P1]") => 1
        case message: String if message.startsWith("[P2]") => 2
        case message: String if message.startsWith("[P3]") => 3
        case _ => 4
      }
    )


}
