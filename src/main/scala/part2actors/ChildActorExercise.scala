package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorExercise extends App {

  // distributed word counting
  object WordCounterMaster {
    def props(name: String): Props = Props(new WordCounterMaster(name))

    case class Initialize(nChildren: Int)

    case class WordCountTask(id: Int, text: String)

    case class WordCountReply(id: Int, count: Int)

  }

  type ChildrenRefsType = List[ActorRef]
  type ReqMapType = Map[Int, ActorRef]

  class WordCounterMaster(name: String) extends Actor {

    import WordCounterMaster._

    override def receive: Receive = receiveHandler(List(), 0, 0, Map())

    def receiveHandler(childrenRefs: ChildrenRefsType, currentChildIndex: Int, currentTaskId: Int,
                       requestMap: ReqMapType): Receive = {
      case Initialize(nChildren: Int) =>
        println(s"[$name master] initializing ...")
        (1 to nChildren).foreach(nthChild => {
          val wordCounterWorker = system.actorOf(WordCounterWorker.props(s"worker-$nthChild"))
          val newChildren: List[ActorRef] = childrenRefs :+ wordCounterWorker
          context.become(receiveHandler(newChildren, currentChildIndex, currentTaskId, requestMap))
        })

      case text: String =>
        println(s"[$name master] I have received: $text - I will send it to child $currentChildIndex")
        // dispatch task preparation
        val task = WordCountTask(currentTaskId, text)
        val childRef = childrenRefs(currentChildIndex)
        val originalSender = sender()
        val newRefMap = requestMap + (currentTaskId -> originalSender)
        // dispatching task
        childRef ! task
        // update parameters
        val newChildIndex = (currentChildIndex + 1) % childrenRefs.length
        val newTaskId = currentTaskId + 1
        context.become(receiveHandler(childrenRefs, newChildIndex, newTaskId, newRefMap))

      case WordCountReply(id, count) =>
        println(s"[$name master] I've received a reply for task id: $id with $count")
        val actualSender = requestMap(id)
        actualSender ! count
        context.become(receiveHandler(childrenRefs, currentChildIndex, currentTaskId, requestMap - id))
    }
  }

  object WordCounterWorker {
    def props(name: String): Props = Props(new WordCounterWorker(name))
  }

  class WordCounterWorker(name: String) extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(id: Int, text: String) =>
        println(s"[$name worker] ${self.path} I've received a task $id with $text")
        sender() ! WordCountReply(id, text.split("").length)
    }
  }

  //  import WordCounterMaster._
  //
  //  val system = ActorSystem("WordCounterSystem")
  //  val wordCounterMaster = system.actorOf(WordCounterMaster.props("master"))
  //
  //  wordCounterMaster ! Initialize(10)

  class TestActor extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case "go" =>
        val master = context.actorOf(WordCounterMaster.props("master"), "master")
        master ! Initialize(3)
        val texts = List("I love Akka", "Scala is super dope", "yes", "me too")
        texts.foreach(text => master ! text)
      case count: Int =>
        println(s"[test actor] I received a reply; $count")
    }
  }

  val system = ActorSystem("RoundRobinWordCountExercise")
  val testActor = system.actorOf(Props[TestActor], "testActor")

  testActor ! "go"

  /*
    create WordCounterMaster
    send Initialize(10) to wordCounterMaster
    send "Akka is awesome" to wordCounterMaster
    wordCounterMaster will send a WordCountTask("...") to one of its children
    child replies with a wordCountReply(3) to the master
    wordCounterMaster replies with 3 to the sender

    requester -> wcm -> wcw
            r <- wcm <-
   */
  // round robin logic
  // 1.2.3.4.5 and 7 tasks
  // 1.2.3.4.5,1,2


}
