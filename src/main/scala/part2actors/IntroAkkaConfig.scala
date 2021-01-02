package part2actors

import com.typesafe.config.ConfigFactory

object IntroAkkaConfig extends App {

  /**
   * 1 - inline configuration
   *
   */

  val configString =
    """
      | akka {
      |   loglevel = "DEBUG"
      | }
      |""".stripMargin

  val config = ConfigFactory.parseString(configString)

}
