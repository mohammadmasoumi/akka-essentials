package part3testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class InterceptingLogSpec extends TestKit(ActorSystem("InterceptingLogSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }
}

object InterceptingLogSpec {

  case class Checkout(item: String, creditCard: String)

  case class AuthorizeCard(creditCard: String)

  case class DispatchOrder(item: String)

  case object PaymentAccepted

  case object PaymentDenied

  case object OrderConfirmed

  class CheckOutActor extends Actor {
    private val paymentManager = context.actorOf(Props[PaymentManager])
    private val fulfillmentManager = context.actorOf(Props[FulfillmentManager])

    override def receive: Receive = awaitingCheckout

    def awaitingCheckout: Receive = {
      case Checkout(item, creditCard) =>
        paymentManager ! AuthorizeCard(creditCard)
        context.become(pendingPayment(item))
    }

    def pendingPayment(item: String): Receive = {
      case PaymentAccepted =>
        fulfillmentManager ! DispatchOrder(item)
        context.become(pendingFulfillment(item))
    }

    def pendingFulfillment(item: String): Receive = {
      case OrderConfirmed => context.become(awaitingCheckout)

    }
  }

  class PaymentManager extends Actor {
    override def receive: Receive = {
      case AuthorizeCard(card: String) =>
        if (card.startsWith("0"))
          sender() ! PaymentDenied
        else
          sender() ! PaymentAccepted
    }
  }

  class FulfillmentManager extends Actor {
    var orderId = 0

    override def receive: Receive = FulfillmentHandler()

    def FulfillmentHandler(orderId: Int = 0): Receive = {
      case DispatchOrder(item: String) =>
        val newOrderId = orderId + 1
        sender() ! OrderConfirmed
        context.become(FulfillmentHandler(newOrderId))
    }
  }

}