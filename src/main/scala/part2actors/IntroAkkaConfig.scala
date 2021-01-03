package part2actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object IntroAkkaConfig extends App {

  class SimpleLoggingActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  /**
   * 1 - inline configuration
   */

  val configString =
    """
      | akka {
      |   loglevel = "INFO"
      | }
      |""".stripMargin

  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("ConfigurationDemo", ConfigFactory.load(config))
  val actor = system.actorOf(Props[SimpleLoggingActor])

  actor ! "A message to remember!"

  /**
   * 2 - configuration from the file
   * Akka automatically assign the config file to the current ActorSystem!
   * /src/main/resources/application.conf
   */

  val defaultConfigFileSystem = ActorSystem("DefaultFileConfigDemo")
  val defaultConfigActor = defaultConfigFileSystem.actorOf(Props[SimpleLoggingActor])

  actor ! "Remember me!"

  /**
   * 3 - separate configuration in the same file
   */

}
