package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.dispatch.{ControlMessage, PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.{Config, ConfigFactory}

object Mailboxes extends App {

  val system = ActorSystem("MailboxDemo", ConfigFactory.load().getConfig("mailboxesDemo"))

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message =>
        log.info(message.toString)
    }
  }

  /**
   * Interesting case #1 - custom priority mailbox
   * P0 -> most important one
   * P1
   * P2
   * P3
   */

  // step 1 - mailbox definition
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

  // step 2 - make it known in the config
  // step 3 - attach the dispatcher to an Actor
  val supportTicketPriorityMailbox = system.actorOf(Props[SimpleActor].withDispatcher("support-ticket-dispatcher"))

  // event PoisonPill will postpone
  supportTicketPriorityMailbox ! PoisonPill

  // then other message will be sent to the dead letter
  Thread.sleep(1000)
  supportTicketPriorityMailbox ! "[P3] this thing would be nice to have!"
  supportTicketPriorityMailbox ! "[P0] this needs to be solved NOW!"
  supportTicketPriorityMailbox ! "[P1] do this when you have time!"

  // [Q]: after which time can I send another message and be prioritized accordingly?
  // [A]: neither you can know nor configure it

  /**
   * Interesting case #2 - control-aware mailbox.
   * we'll use UnboundedControlAwareMailbox
   */

  // step 1 - mark important messages as control messages
  case object ManagementTicket extends ControlMessage

  /**
   * step 2 - configure who gets the mailbox
   *  - make the actor attach to the mailbox
   */
  // method #1
  val controlAwareActor = system.actorOf(
    Props[SimpleActor].withDispatcher("control-mailbox"), "controlAwareSimpleActor"
  )
//  controlAwareActor ! "[P0] this needs to be solved NOW!"
//  controlAwareActor ! "[P1] do this when you have time!"
//  controlAwareActor ! ManagementTicket

  // method #2 - using deployment config
  val alternativeControlAwareActor = system.actorOf(Props[SimpleActor], "altControlAwareActor")
  alternativeControlAwareActor ! "[P0] this needs to be solved NOW!"
  alternativeControlAwareActor ! "[P1] do this when you have time!"
  alternativeControlAwareActor ! ManagementTicket






}
