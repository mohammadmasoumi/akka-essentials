package part3testing

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

// BEST PATTERN: end with `Spec` key
class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {


}
