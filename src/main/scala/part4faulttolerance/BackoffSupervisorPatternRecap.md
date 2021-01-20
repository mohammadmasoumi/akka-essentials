# BackoffSupervisorPatternRecap

### Pain: the repeated restarts of actors
  - restarting immediately might be useless
  - everyone attempting at the same time can kill resources again
  
### Create backoff supervision for exponential delays between attempts
```scala
BackoffSupervisor.props(
    Backoff.onFailure( // control when backoff kicks in
      Props[FileBasedPersistentActor],
      "simpleBackoffAccor",
      3 seconds, // then 6s, 12s, 24s, no more(30s)
      30 seconds, // min and max delay 
      0.2 // randomness factor
    )
  )
```

