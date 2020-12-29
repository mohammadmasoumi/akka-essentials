package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ChildActorExercise extends App {

  // distributed word counting
  object WordCounterMaster {
    def props(name: String): Props = Props(new WordCounterMaster(name))

    case class Initialize(nChildren: Int)

    case class WordCountTask(/* TODO */ text: String)

    case class WordCountReply /* TODO */ (count: Int)

  }

  class WordCounterMaster(name: String) extends Actor {
    override def receive: Receive = ???
  }

  object WordCounterWorker {
    def props(name: String): Props = Props(new WordCounterWorker(name))
  }

  class WordCounterWorker extends Actor {
    override def receive: Receive = ???
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
