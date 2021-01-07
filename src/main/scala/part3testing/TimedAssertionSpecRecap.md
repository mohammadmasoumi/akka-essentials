# TimedAssertionSpecRecap

 - Put a time cap on the assertions
    ```scala
    within(500.millis, 1.second) {
    // everything in here must pass
    }
    ```
 - Receive and process messages during a time window 
    ```scala
    val results = receiveWhile[Int](max = 2.seconds, idle = Duration.Zero, messages = 10) {
     case WorkResult(...) => // some value
   }
    ```
   > Then do assertions based on the results
  
  - `TestProbes` don't listen to `within` block.                                                                                                                                                                                                                    >
