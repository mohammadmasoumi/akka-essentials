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

  defaultConfigActor ! "Remember me!"

  /**
   * 3 - separate configuration in the same file
   */
  val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
  val specialConfigSystem = ActorSystem("specialConfigSystemDemo", specialConfig)
  val specialConfigActor = specialConfigSystem.actorOf(Props[SimpleLoggingActor])

  specialConfigActor ! "Remember me! I'm special!"

  /**
   * 4 - separate configuration in the another file
   */
  val secretConfig = ConfigFactory.load("secretFolder/secretConfiguration.conf")
  val secretConfigSystem = ActorSystem("secretConfigSystemDemo", secretConfig)
  val secretConfigActor = secretConfigSystem.actorOf(Props[SimpleLoggingActor])

  println(s"separate config log level: ${secretConfig.getString("akka.loglevel")}")
  secretConfigActor ! "Remember me! I'm secret!"


  /**
   * 5 - different file formats
   * JSON, Properties
   */
  val jsonConfig = ConfigFactory.load("json/jsonConfig.json")
  val jsonConfigSystem = ActorSystem("jsonConfigSystemDemo", jsonConfig)
  val jsonConfigActor = jsonConfigSystem.actorOf(Props[SimpleLoggingActor])

  println(s"json config: ${jsonConfig.getString("aJsonProperty")}")
  println(s"json config: ${jsonConfig.getString("akka.loglevel")}")
  jsonConfigActor ! "json config actor!"

  val propertiesConfig = ConfigFactory.load("props/propsConfiguration.properties")
  val propertiesConfigSystem = ActorSystem("propsConfigSystemDemo", propertiesConfig)
  val propertiesConfigActor = propertiesConfigSystem.actorOf(Props[SimpleLoggingActor])

  println(s"properties config: ${propertiesConfig.getString("my.simpleProperty")}")
  println(s"properties config: ${propertiesConfig.getString("akka.loglevel")}")
  propertiesConfigActor ! "property config actor!"

}
