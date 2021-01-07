# BasicSpecRecap

## Test suit definition

 - recommended: to use a companion as well
   ```scala
   import akka.actor.ActorSystem
   import akka.testkit.{ImplicitSender, TestKit}
   import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
   class MySuperSpec extends TestKit(ActorSystem("MySpec"))
     with ImplicitSender
     with WordSpecLike
     with BeforeAndAfterAll
   ```
 
 - Test structure
    ```scala
    "The thing being tested" should {
         "do this" in {
           // testing scenario
         }
         "do this" in {
            // another testing scenario
          }      
   }
   ```
   
 - recommended: to use a companion as well
   ```scala
   val message = expectMsg("hello") // default timeout: 3 seconds(configurable)
   ```
   ```scala
   expectNoMsg(1.second)
   // or
   expectNoMsg(1 second)
   ```
   ```scala
   val message = expectMsgType[String] 
   ```
   ```scala
   val message = expectMsgAnyOf("hello", "world") 
   ```
   ```scala
   val message = expectMsgAllOf("hello", "world") 
   ```
   ```scala
   expectMsgPF(){
    case "hello" =>
   } 
   ```
   