package publish

import akka.actor.Actor
import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import akka.cluster.pubsub.DistributedPubSub
import hackernews4s.v0.Item

class Publisher extends Actor {  
  val mediator = DistributedPubSub(context.system).mediator 

    def receive = {
      case s: String =>
        val bFR = io.Source.fromFile(s)
        for (line <- bFR.getLines) {
          val columns = line.split(",").map(_.trim)
            // Strictly using Publisher to send data from file
            // This is acting as our mock datacenter for now
            if(s.contains("comet"))
            {
              mediator ! Publish("content", s"${columns(0)}|${columns(3)}|${columns(6)}|${columns(9)}")
            }
            else
            {
              // Columns don't match for both files
              mediator ! Publish("content", s"${columns(0)}|${columns(3)}|${columns(6)}}")
            }
          mediator ! 5
          context.stop(self)  //exit code (shut actor down)
        }
  }
}