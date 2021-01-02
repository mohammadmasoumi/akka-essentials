package part2actors

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

}
