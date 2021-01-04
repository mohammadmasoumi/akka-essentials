package part3testing

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

// BEST PATTERN: end with `Spec` key
class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll {

  // setup
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system) // system is a member of test kit.
  }
  "The thing being tested" should { // test suit
    "do this" in { // testcase-1
      // testing scenario
    }
    "do another thing" in { // testcase-2
      // testing scenario
    }
  }

}
