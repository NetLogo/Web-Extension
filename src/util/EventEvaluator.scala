package org.nlogo.extensions.web.util

import
  scala.{ concurrent, reflect },
    concurrent.{ Await, duration },
      duration._,
    reflect.ClassTag

import
  akka.{ actor, pattern, util },
    actor.{ Actor, ActorRef, ActorSystem, Props },
    pattern.ask,
    util.Timeout

import
  org.nlogo.{ api, awt, nvm }, //, swing },
    api.ReporterRunnable,
    awt.EventQueue,
    nvm.Workspace //,
    //swing.Implicits

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/17/12
 * Time: 1:12 PM
 */

object EventEvaluator {

  sealed protected trait EventActorProtocol
  protected object EventActorProtocol {
    case object Evaluate extends EventActorProtocol
  }

  import EventActorProtocol._

  private val system = ActorSystem("LoggingSystem")

  protected class EventEvaluationActor[T, U](stream: T, func: (T) => U) extends Actor {
    //import Implicits.thunk2runnable
    override def receive = {
      case Evaluate =>
        EventQueue.invokeLater {
          val s = sender // Odd that I have to close over `sender` here, lest the reply is never received
          () => s ! func(stream)
        }
    }
  }

  // The stupid `start` method only returns an `Actor`; can't have a more specific return type --JAB (10/23/12)
  protected def generateActor[T, U](stream: T, hook: (T) => U) : ActorRef = system.actorOf(Props(new EventEvaluationActor(stream, hook)))

  def apply[T, U : ClassTag](stream: T, hook: (T) => U) : U = {
    implicit val timeout = Timeout(20 seconds)
    Await.result((generateActor(stream, hook) ? Evaluate).mapTo[U], timeout.duration)
  }

  // This _cannot_ run within an actor; if it does, NetLogo freezes (for some reason)
  // At least, that's the case with exporting the interface --JAB (10/23/12)
  def withinWorkspace[T, U](stream: T, hook: (T) => U, workspace: Workspace) : U = {
    workspace.waitForResult[U] (
      new ReporterRunnable[U] {
        override def run() : U = hook(stream)
      }
    )
  }

}

