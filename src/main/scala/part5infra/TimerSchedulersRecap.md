# TimerSchedulersRecap

 - Schedule an action at a defined point in the future.
   ```scala
   val schedule: Cancellable = system.scheduler.scheduleOnce(1 second) { /* code here. */ }    
   ```
 - Repeated action 
   ```scala
   val schedule: Cancellable = system.scheduler.schedule(1 second, 2 seconds) { /* code here. */ }    
   schedule.cancel()
   ```
    - **`1 second` initial delay**
    - **`2 second` interval delay**
 - **Timers:** schedule messages to **self**, from within.
   ```scala
   timers.startSingleTimer(TimerKey, Start, 500 millis)
   timers.startperiodicTimer(TimerKey, Start, 500 millis)
   timers.cancel(TimerKey)
   ```