package part3testing

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{EventFilter, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import part3testing.InterceptingLogSpec.Checkout

class InterceptingLogSpec extends TestKit(ActorSystem("InterceptingLogSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val item = "Rock the JVM Akka course"
  val creditCard = "1234-1234-1234-1234"

  "A checkout flow" should {
    "correctly log the dispatch of an order" in {
      EventFilter.info(pattern = s"Order [0-9]+ for item $item has been dispatched!", occurrences = 1) intercept {
        // our test code
        val checkoutRef = system.actorOf(Props[InterceptingLogSpec])
        checkoutRef ! Checkout(item, creditCard)
      }
    }
  }

}

object InterceptingLogSpec {

  case class Checkout(item: String, creditCard: String)

  case class AuthorizeCard(creditCard: String)

  case class DispatchOrder(item: String)

  case object PaymentAccepted

  case object PaymentDenied

  case object OrderConfirmed

  class CheckOutActor extends Actor with ActorLogging {
    private val paymentManager = context.actorOf(Props[PaymentManager])
    private val fulfillmentManager = context.actorOf(Props[FulfillmentManager])

    override def receive: Receive = awaitingCheckout

    def awaitingCheckout: Receive = {
      case Checkout(item, creditCard) =>
        log.info(s"Checkout processing for item: $item has been initialized successfully!")
        paymentManager ! AuthorizeCard(creditCard)
        context.become(pendingPayment(item))
    }

    def pendingPayment(item: String): Receive = {
      case PaymentAccepted =>
        log.info(s"Payment for item $item has been done successfully!")
        fulfillmentManager ! DispatchOrder(item)
        context.become(pendingFulfillment(item))
      case PaymentDenied =>
        log.info(s"Payment for item $item has been failed!")
        context.become(awaitingCheckout)
    }


    def pendingFulfillment(item: String): Receive = {
      case OrderConfirmed => context.become(awaitingCheckout)

    }
  }

  class PaymentManager extends Actor with ActorLogging {
    override def receive: Receive = {
      case AuthorizeCard(card: String) =>
        log.info(s"Authorizing card: $card")
        if (card.startsWith("0")) {
          log.info(s"Card $card has been authorized successfully!")
          sender() ! PaymentDenied
        } else {
          log.info(s"Card $card is unauthorized!")
          sender() ! PaymentAccepted
        }
    }
  }

  class FulfillmentManager extends Actor with ActorLogging {
    var orderId = 0

    override def receive: Receive = FulfillmentHandler()

    def FulfillmentHandler(orderId: Int = 0): Receive = {
      case DispatchOrder(item: String) =>
        val newOrderId = orderId + 1
        log.info(s"Order $newOrderId for item $item has been dispatched!")

        sender() ! OrderConfirmed
        context.become(FulfillmentHandler(newOrderId))
    }
  }

}