package publish

import akka.actor.ActorSystem
import akka.actor.Props
import akka.cluster.Cluster

// Demonstrates a publisher-subscriber model using akka's
//    distributed pub-sub model
object Main {
  def main(args: Array[String]): Unit = {
    val sysName = "mySub"
    
    // create address with host name "mySub"
    val sys = ActorSystem(sysName)
    
    //fluff
    println("What is your name?")
    val name = scala.io.StdIn.readLine()
    
    // For this test I'm using part of NASA's data
    //    in this case the uesr can either
    //    subscribe to get news about 'meteorites'
    //    or get news about 'comets'
    println("Enter 1 for meteorites or 2 for comets!")
    val response = scala.io.StdIn.readLine()
    if(response == "1")
    {
      val sub1 = sys.actorOf(Props(new Subscriber(name)), name)
      sub1 ! 1
      
      val pub = sys.actorOf(Props[Publisher], "publisher")    
      pub ! "Meteorite_Landings.csv"  // send filename for meteorite information
    }
    else
    {
      val sub2 = sys.actorOf(Props(new Subscriber(name)), name)
      
      val pub2 = sys.actorOf(Props[Publisher], "publisher")
      pub2 ! "comet_stats.csv" // send filename for comet information
    }
  }
}
