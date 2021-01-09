# InterceptingLogSpecRecap

 - User ``EventFilter`` to intercept logs
    ```scala
   EventFilter.info("my log message", occurrences = 1) intercept {
     // your test here.
   }
    ```
    > works for all log levels: **debug**, **info**, **warning**, **error**.

 - Intercept exceptions.
    ```scala
    EventFilter[RuntimeException](occurrences = 1) intercept {
     // your test here.
    }
     ```
 - Good for integration tests where
    - it's hard to do message-based testing.
    - there is logs to inspect.
   