package org.nlogo.extensions.web.util

import scala.concurrent.{ Await, Future }

import org.nlogo.api.ReporterRunnable
import org.nlogo.nvm.Workspace

object EventEvaluator {

  def apply[T, U](stream: T, hook: (T) => U): U = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._
    Await.result(Future{ hook(stream) }, 20 seconds)
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

