# SuperVisionSpecRecap

 - Parents decide on their children's failure with a supervision strategy.
    ```scala
    override val supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: RuntimeException => Resume
      case _: Exception => Escalate // increase rapidly.
    }
    ```
   decider = **```PartialFunction[Throwable, Directive]```**
   
 - **Result:**
    - **fault tolerance**
    - **self-healing**