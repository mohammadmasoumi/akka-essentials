package part6patterns

object StashDemo extends App {

    /**
      ResourceActor
       - Open => it can receivve read/write requests to the resource.
       - otherwise => it will postpone all read/write requests until the state is open.

       ResourceActor is closed.
         - Open => it will switch to the open state.
         - Read, Write messages are POSTPONED.
       
       ResourceActor is open.
         - Read/Write are handled.
         - Close => switch to the closed state.

       [Open, Read, Read, Write]
         
    */

    case object Open
    case object Close
    case object Write(data: String)

}