package org.nlogo.extensions.web

import java.net.URL

import org.nlogo.api.{ Argument, Command, Context, ExtensionException, Workspace }
import org.nlogo.core.Syntax.{ commandSyntax, StringType }
import org.nlogo.nvm.ExtensionContext
import org.nlogo.window.GUIWorkspace

object ImportDrawing extends WebPrimitive with Command {

  override def getSyntax = commandSyntax(List(StringType))

  override def perform(args: Array[Argument], context: Context): Unit = carefully {
    EnsuranceAgent.ensuringGUIWorkspace(context.workspace) { (guiWS: GUIWorkspace) =>
      val dest = args(0).getString
      using(new URL(dest).openStream()) {
        guiWS.importDrawing(_)
      }
    }
  }

}

object ImportDrawingFine extends WebPrimitive with Command {

  override def getSyntax = commandSyntax(List(StringType))

  override def perform(args: Array[Argument], context: Context): Unit = carefully {
    EnsuranceAgent.ensuringGUIWorkspace(context.workspace) { (guiWS: GUIWorkspace) =>
      val dest      = args(0).getString
      val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
      val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
      processResponse(mkRequest(dest, reqMethod, paramMap, Map.empty)) {
        case (response, _) => guiWS.importDrawing(response)
      }
    }
  }

}

private object EnsuranceAgent {
  def ensuringGUIWorkspace[T](ws: Workspace)(f: (GUIWorkspace) => T): T = {
    ws match {
      case guiWS: GUIWorkspace =>
        f(guiWS)
      case other =>
        val message = s"Cannot use this primitive from any type of workspace by a `GUIWorkspace`; you're using a ${other.getClass.getName}."
        throw new UnsupportedOperationException(message)
    }
  }
}
