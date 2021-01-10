# SynchronousTestingSpecRecap


## Synchronous tests
 
 - all messages are handled in the calling thread.
 - all the communications are under the **`common thread`**.
 
### Option1: TestActorRef
 - needs an implicit **`ActorSystem`**
    ```scala
   val syncActor = TestActorRef[MyActor](Props[MyActor])   
    ```  
   ```scala
   assert(syncActor.underlyingActor.member == 1)
    ```
   ```scala
   syncActor.receive(Tick)
    ```
    
### Option2: CallingThreadDispatcher
```scala
val syncActor = system.actorOf(Props[MyActor].withDispatcher(CallingThreadDispatcher.Id))
```
