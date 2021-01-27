package part6patterns

object StashDemo extends App {

    /**
      ResourceActor
       - open => it can receivve read/write requests to the resource.
       - otherwise => it will postpone all read/write requests until the state is open.

       ResourceActor is closed.
    */

    case object Open
    case object Close
    case object Write(data: String)

}