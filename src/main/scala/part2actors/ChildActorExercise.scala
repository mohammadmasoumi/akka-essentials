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

  type ChildrenReds = List[ActorRef]

  class WordCounterMaster(name: String) extends Actor {

    import WordCounterMaster._

    override def receive: Receive = receiveHandler(List(), 0, 0)

    def receiveHandler(childrenRefs: ChildrenReds, currentChildIndex: Int, currentTaskId: Int): Receive = {
      case Initialize(nChildren: Int) =>
        (1 to nChildren).foreach(nthChild => {
          val wordCounterWorker = system.actorOf(WordCounterWorker.props(s"worker-$nthChild"))
          val newChildren: List[ActorRef] = childrenRefs :+ wordCounterWorker
          context.become(receiveHandler(newChildren, currentChildIndex, currentTaskId))
        })
      case WordCountReply(count) => ???
      case text: String =>
        // dispatch task preparation
        val task = WordCountTask(currentTaskId, text)
        val childRef = childrenRefs(currentChildIndex)
        // dispatching task
        childRef ! task
        // update parameters
        val newChildIndex = (currentChildIndex + 1) % childrenRefs.length
        val newTaskId = currentTaskId + 1
        context.become(receiveHandler(childrenRefs, newChildIndex, newTaskId))
    }
  }

  object WordCounterWorker {
    def props(name: String): Props = Props(new WordCounterWorker(name))
  }

  class WordCounterWorker extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(taskId: Int, text: String) => sender() ! WordCountReply(taskId, text.split("").length)
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
