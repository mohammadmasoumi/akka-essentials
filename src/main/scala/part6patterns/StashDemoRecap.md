# StashDemoRecap


## Put messages aside for later.

 - mix-in the **stash trait**.
   ```scala
   extends Actor ... with Stash
   ```
 - stash the message away.
   ```scala
   stash()
   ```
 - empty the stash
   ```scala
   unstashAll()
   ```

## Things to be careful about.

 - potential memory bounds on Stash.
 - potential mailbox bounds when unStashing.
 - no stashing twice.
 - the **Stash** trait overrides **preRestart** so must be **mixed-in** last. 