import actors.{EventsListAfterMsg, LogItem, AnalyticsLogger}
import akka.actor.ActorSystem
import akka.actor.Actor
import akka.testkit.{TestKit, TestActorRef}
import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpecLike
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender

class MockActor(newVisitInterval:Int) extends AnalyticsLogger {
  override val newVisitLimit = newVisitInterval
}

class AnalyticsLoggerTest(_system: ActorSystem)
  extends TestKit(_system)
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("AnalyticsLoggerTest"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "Newcomer event" must {
    val newVisitInterval = 100

    val actorRef = TestActorRef(new MockActor(newVisitInterval))

    val cuttentTime = System.currentTimeMillis / 1000

    // interval of time when comes next signal
    val nextSignalInterval = 5

    "arise when new client will come, i must get newcomerEvent" in {
      actorRef ! LogItem("firstDevice", 1, 1, cuttentTime)
      assert(actorRef.underlyingActor.events.size == 1)
    }

    "does not arise when comes second signal" in {
      actorRef ! LogItem("firstDevice", 1, 1, cuttentTime + nextSignalInterval)
      assert(actorRef.underlyingActor.events.size == 1)
    }

    "arise when new client returns in newVisitLimit time" in {
      actorRef ! LogItem("firstDevice", 1, 1, cuttentTime + nextSignalInterval + newVisitInterval)
      assert(actorRef.underlyingActor.events.size == 2)
    }

    "be 3 time" in {
      actorRef ! LogItem("firstDevice", 1, 1, cuttentTime + nextSignalInterval + 2*newVisitInterval)
      actorRef ! LogItem("firstDevice", 1, 1, cuttentTime + nextSignalInterval + 2*newVisitInterval + 1)
      actorRef ! LogItem("firstDevice", 1, 1, cuttentTime + 2*nextSignalInterval + 2*newVisitInterval + 1)
      assert(actorRef.underlyingActor.events.size == 3)
    }
  }
}
