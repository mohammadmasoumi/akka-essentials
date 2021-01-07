# ChildActorRecap

 - actors can create other actors
 ```scala
 context.actorOf(Props[MyActor], "child")
 ```
 - top-level supervisors(guardians)
    - /system
    - /user
    - the root guardian / 
 - actor paths
 ```
 /user/parent/child
 ```
 - actor selections
 ```scala
 system.actorSelection("/user/parent/child")
 context.actorSelection("/user/parent/child")
 ```
