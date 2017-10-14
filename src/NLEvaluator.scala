package org.nlogo.extensions.web

import org.nlogo.api.{ ReporterRunnable, Workspace }

object NLEvaluator {
  def apply[T, U](workspace: Workspace)(stream: T)(hook: (T) => U): U = {
    workspace.waitForResult[U] (
      new ReporterRunnable[U] {
        override def run(): U = hook(stream)
      }
    )
  }
}
