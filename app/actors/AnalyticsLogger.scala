package actors

import akka.actor.Actor
import scala.collection.mutable

/**
 *
 * @author auldanov
 */

case class LogItem(deviceId: String, x: Int, y: Int)

/**
 * Main event type, it has a unique deviceId and time of arising
 */
case class Event(deviceId: String, time: Long)

/**
 * It's one of events types, arises when detected new device,
 * device which isn't known before, or we have not logs at last 5 hours.
 */
class NewcomerEvent(deviceId: String, time: Long) extends Event(deviceId, time)

/**
 * One of events types, arises when we haven't  recive a signal within 5 minutes
 */
class LeaveEvent(deviceId: String, time: Long) extends Event(deviceId, time)

/**
 * One of events types, arises when we detect man who has many devices
 */
class PluralDeviceDetectEvent(deviceId: String, time: Long) extends Event(deviceId, time)

/**
 * One of events types, arises when we detect that all coordinates of device located
 * inside of circle certain radius, within 5 minutes
 */
class StagnateEvent(deviceId: String, time: Long) extends Event(deviceId, time)

case class EventsListAfterMsg(timestamp: Long)

class AnalyticsLogger extends Actor {

  val events = mutable.ArrayBuffer[Event]()

  val logs = mutable.Map[String,(Long, List[Event])]()

  def receive = {
    case LogItem(deviceId, x, y) =>  {
      // TODO check is this newcomer

      // TODO check stagnate

      // TODO add log

      // TODO check leave

      // TODO check plural devices
    }
    case EventsListAfterMsg(timestamp) => sender ! events.filter(_.time > timestamp)
  }
}