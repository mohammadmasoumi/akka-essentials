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
        println("[master] initializing ...")
        (1 to nChildren).foreach(nthChild => {
          val wordCounterWorker = system.actorOf(WordCounterWorker.props(s"worker-$nthChild"))
          val newChildren: List[ActorRef] = childrenRefs :+ wordCounterWorker
          context.become(receiveHandler(newChildren, currentChildIndex, currentTaskId, requestMap))
        })
      case text: String =>
        println(s"[master] I have received: $text - I will send it to child $currentChildIndex")
        // dispatch task preparation
        val task = WordCountTask(currentTaskId, text)
        val childRef = childrenRefs(currentChildIndin
        val originalSender = sender()
        val newRefMap = requestMap + (currentTaskId -> originalSender)
        // dispatching task
        childRef ! task
        // update parameters
        val newChildIndex = (currentChildIndex + 1) % childrenRefs.length
        val newTaskId = currentTaskId + 1
        context.become(receiveHandler(childrenRefs, newChildIndex, newTaskId, newRefMap))
      case WordCountReply(taskId, count) =>
        val actualSender = requestMap(taskId)
        actualSender ! count
        context.become(receiveHandler(childrenRefs, currentChildIndex, currentTaskId, requestMap - taskId))
    }
  }

  object WordCounterWorker {
    def props(name: String): Props = Props(new WordCounterWorker(name))
  }

  class WordCounterWorker extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(taskId: Int, text: String) =>
        println(s"[worker] ${self.path} I've received a task with ${text}")
        sender() ! WordCountReply(taskId, text.split("").length)
    }
  }

  import WordCounterMaster._

  val system = ActorSystem("WordCounterSystem")
  val wordCounterMaster = system.actorOf(WordCounterMaster.props("master"))

  wordCounterMaster ! Initialize(10)

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
