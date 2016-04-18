package publish

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.Address
import akka.cluster.pubsub.DistributedPubSubMediator
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.MemberStatus

class Subscriber(name: String) extends Actor with ActorLogging {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Subscribe("content", self)
  val sub = context.actorOf(Publisher.props(self.path.name), name)
  
  def receive = {
    case 1 => println("Hello %s! You are joining the chat. Please enter -e to exit.".format(name))
    case s: String =>
      sub ! Publisher.Publish(s)
    case SubscribeAck(Subscribe("content", None, `self`)) =>
      println("subscribing");
  }
}

class Listener extends Actor with ActorLogging {
  val cluster = Cluster(context.system)
  
  override def preStart(): Unit = 
    cluster.subscribe(self, classOf[MemberEvent])
  override def postStop(): Unit =
    cluster unsubscribe self
    
  var nodes = Set.empty[Address]
  
  def receive = {
    case state: CurrentClusterState =>
      nodes = state.members.collect {
        case u if u.status == MemberStatus.Up => u.address
      }
      
    case MemberUp(user) =>
      nodes += user.address
      println("User {} is here! There are {} nodes in this cluster.", user.address, nodes.size)
    case MemberRemoved(user, _) =>
      nodes -= user.address
      println("User {} has left. There are now {} nodes in this cluster.", user.address, nodes.size)
    case _: MemberEvent =>  
  }
}