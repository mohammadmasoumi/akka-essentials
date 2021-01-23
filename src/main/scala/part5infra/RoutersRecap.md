# RoutersReca 

 - Goal: spread/delegate messages in between N identical actors
 
### Methods
 
 - Router method #1: manual - ignored 
 - Router method #2: pool router
   ```scala
   val poolMaster = system.actorOf(RoundRobinPool(5).props(Props[Slave]), "simplePoolMaster")
   ``` 
   ```scala
   val poolMaster2 = system.actorOf(FromConfig.props(Props[Slave]), "poolMaster2")
   ```
 - Router method #3: manual - ignored 
 
 

 
