# TestProbeSpecRecap

 - `TestProbes` are useful for interactions with multiple actors.
    ```scala
    val probe = TestProbe("TestProbeName")
    ```
 - Can send messages or reply
    ```scala
    probe.send(actorUnderTest, "a message")
    ```
    ```scala
    probe.reply("a message") // send to it's last sender
    ``` 
 - Has the same assertions as the testActor
 - Can switch other actors