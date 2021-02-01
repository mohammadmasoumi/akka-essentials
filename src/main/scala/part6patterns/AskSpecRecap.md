# AskSpec

 - Use ask when you expect a single response
   ```scala
   val askFuture = actor ? Read("Daniel")
   ```
 - Process the future
   ```scala
   askFuture.onComplete {
     case ...
   }
   ```
 - Pipe it
   ```scala
   askFuture.mapTo[String].pipeTo(actor)
   ```
 - Be VERY careful with the **`ask`** and **`Futures`**.
   - NEVER call methods or access mutable state in callbacks.