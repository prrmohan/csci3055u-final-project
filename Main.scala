package publish

import akka.actor.ActorSystem
import akka.actor.Props
import akka.cluster.Cluster

object Main {
  def main(args: Array[String]): Unit = {
    val sysName = "myConvo"
    val sys = ActorSystem(sysName)
    val address = Cluster(sys).selfAddress
    println("What is your name?")
    val name = scala.io.StdIn.readLine()
    sys.actorOf(Props[Listener], "memberListener")
    val sub1 = sys.actorOf(Props(new Subscriber(name)), name)
    sub1 ! 1
        
    val sys2 = ActorSystem(sysName)
    Cluster(sys2).join(address)
    println("Enter your name:")
    val name2 = scala.io.StdIn.readLine()
    val sub2 = sys2.actorOf(Props(new Subscriber(name2)), name2)
    sub2 ! 1
    
    val sys3 = ActorSystem(sysName)
    Cluster(sys3).join(address)
    println("Enter your name:")
    val name3 = scala.io.StdIn.readLine()
    val sub3 = sys3.actorOf(Props(new Subscriber(name3)), name3)
    sub3 ! 1
    sub3 ! "test"
    sub2 ! "test1"
    sub2 ! "yes"
    sub2 ! "hello"
    
    var exit = false
    while(!exit)
    {
      val msg = scala.io.StdIn.readLine()
      if(msg == "-e")
      {
        exit = true
      }
      else
      {
        sub1 ! msg
      }      
    }
   sys.shutdown()
  }
}
