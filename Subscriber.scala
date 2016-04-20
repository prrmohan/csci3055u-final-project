package publish

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Address
import akka.cluster.pubsub.DistributedPubSubMediator
//import akka.cluster.pubsub.DistributedPubSubMediator.Put
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.MemberStatus

// Subscriber model.
class Subscriber(name: String) extends Actor with ActorLogging {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  import DistributedPubSubMediator.Send
  val mediator = DistributedPubSub(context.system).mediator
  // Subscribes to channel 'content'. Any publish made to the channel 'content'
  //    should be received by this mediator
  mediator ! Subscribe("content", self)
  
  def receive = {
    // Fluff
    case 1 => println("Hello %s! You are joining the chat. Please enter -e to exit.".format(name))
    case 5 => context.stop(self)
    // Print out message received
    case s: String =>
      println("Received!")
      Thread.sleep(1)
      println(s)
    case SubscribeAck(Subscribe(content, None, `self`)) =>
      println("subscribing to %s".format(content))
  }
}