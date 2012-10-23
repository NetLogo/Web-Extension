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

  protected class EventEvaluationActor[T, U](stream: T, func: (T) => U) extends Actor {
    import org.nlogo.swing.Implicits.thunk2runnable
    def act() {
      loop {
        react {
          case Start => org.nlogo.awt.EventQueue.invokeLater{ () => reply(func(stream)); }
        }
      }
    }
  }

  def apply[T, U](stream: T, hook: (T) => U) : U = {
    ((new EventEvaluationActor(stream, hook)).start() !! Start)().asInstanceOf[U]
  }

}

