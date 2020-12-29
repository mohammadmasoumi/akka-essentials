# akka-essentials

Recently, I started learning [Scala][1] snd [functional programming][3], 
therefore I've created this project to share what I've learned.


## subjects

 - [advanced scala and multithreading recap][4]
 - [introduction to actors][5]


## child actor(recap)

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

### Courses

 - [**akka-essentials**][3]



[1]: https://www.scala-lang.org/
[2]: https://en.wikipedia.org/wiki/Functional_programming
[3]: https://www.udemy.com/course/rock-the-jvm-scala-for-beginners/learn/lecture/7660552#overview
[4]: https://github.com/mohammadmasoumi/akka-essentials/tree/main/src/main/scala/part1recap
[5]: https://github.com/mohammadmasoumi/akka-essentials/tree/main/src/main/scala/part2actors