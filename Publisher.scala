package publish

import akka.actor.Actor
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.cluster.pubsub.DistributedPubSub

object Publisher {
  def props(name: String): Props = Props(classOf[Publisher], name)
  
  case class Publish(message: String)
  case class Message(from: String, txt: String)
}

class Publisher(name: String) extends Actor {  
  val mediator = DistributedPubSub(context.system).mediator
  val group = "chat"
  mediator ! Subscribe(group, self)
  println(s"$name joined chat.")
  
  def receive = {
    case Publisher.Publish(message) =>
      mediator ! Publish(group, Publisher.Message(name, message))
      
    case Publisher.Message(from, txt) =>
      val dir = if(sender == self) " >>> " else s" << $from:"
      println(s"$name $dir $txt")
  } 
}