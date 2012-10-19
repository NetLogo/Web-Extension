package org.nlogo.extensions.web.prim.util

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/17/12
 * Time: 1:12 PM
 */

object EventEvaluator {

  import actors.Actor

  sealed protected trait TempActorProtocol
  protected object TempActorProtocol {
    object Start extends TempActorProtocol
  }

  import TempActorProtocol.Start

  protected class EventEvaluationActor[T](stream: T, func: (T) => Unit) extends Actor {
    import org.nlogo.swing.Implicits.thunk2runnable
    def act() {
      loop {
        react {
          case Start => org.nlogo.awt.EventQueue.invokeLater{ () => func(stream); reply() }
        }
      }
    }
  }

  def apply[T](stream: T, hook: (T) => Unit) {
    ((new EventEvaluationActor(stream, hook)).start() !! Start)()
  }

}

