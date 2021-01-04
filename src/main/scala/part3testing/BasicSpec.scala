package part3testing

import akka.actor.ActorSystem
import akka.testkit.TestKit

// BEST PATTERN: end with `Spec` key
class BasicSpec extends TestKit(ActorSystem("BasicSpec")) {

}
