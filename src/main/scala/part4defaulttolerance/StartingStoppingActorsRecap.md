# StartingStoppingActorsRecap


### Using `context.stop`

 - **asynchronous** - actor may continue to receive messages until actually stopped.
   ```scala
   context.stop(child) 
   ```
 - will recursively stop children(**asynchronously**)
   ```scala
   context.stop(child) 
   ```
   
### Using special messages

 - ```scala
   actor ! PoisonPill 
   ```
 - makes the actors throw an **ActorKilledException** 
    ```scala
   actor ! PoisonPill 
    ```
### Death watch

```scala
context.watch(actor)
```