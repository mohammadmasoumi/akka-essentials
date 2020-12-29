package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorExercise extends App {

  // distributed word counting
  object WordCounterMaster {
    def props(name: String): Props = Props(new WordCounterMaster(name))

    case class Initialize(nChildren: Int)

    case class WordCountTask(/* TODO */ text: String)

    case class WordCountReply /* TODO */ (count: Int)

  }

  class WordCounterMaster(name: String) extends Actor {

    import WordCounterMaster._

    override def receive: Receive = receiveHandler()

    def receiveHandler(childrenRefs: List[ActorRef] = List(), currentChildIndex: Int = 0): Receive = {
      case Initialize(nChildren: Int) =>
        (1 to nChildren).foreach(nthChild => {
          val wordCounterWorker = system.actorOf(WordCounterWorker.props(s"worker-$nthChild"))
          val newChildren: List[ActorRef] = childrenRefs :+ wordCounterWorker
          context.become(receiveHandler(newChildren, currentChildIndex))
        })
      case text: String =>
        val task = WordCountTask(text)
        val childRef = childrenRefs(currentChildIndex)
        childRef ! task
        context.become(receiveHandler(childrenRefs, (currentChildIndex + 1) % childrenRefs.length))
    }
  }

  object WordCounterWorker {
    def props(name: String): Props = Props(new WordCounterWorker(name))
  }

  class WordCounterWorker extends Actor {

    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(text: String) => sender() ! WordCountReply(text.split("").length)
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
