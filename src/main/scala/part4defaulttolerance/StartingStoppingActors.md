# StartingStoppingActors


### Using `context.stop`

 - **asynchronous** - actor may continue to receive messages until actually stopped.
   ```scala
   context.stop(child) 
   ```
 - will recursively stop children(**asynchronously**)
   ```scala
       context.stop(child) 
   ```
   
