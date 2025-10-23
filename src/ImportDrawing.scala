package org.nlogo.extensions.web

import java.net.URL

import org.nlogo.api.{ Argument, Command, Context, ExtensionException, Workspace }
import org.nlogo.core.Syntax.{ commandSyntax, ListType, StringType }
import org.nlogo.nvm.ExtensionContext

object ImportDrawing extends WebPrimitive with Command {

  override def getSyntax = commandSyntax(List(StringType))

  override def perform(args: Array[Argument], context: Context): Unit = carefully {
    if (context.workspace.workspaceContext.workspaceGUI) {
      val dest = args(0).getString
      using(new URL(dest).openStream()) {
        context.workspace.importDrawing(_)
      }
    }
  }

}

object ImportDrawingFine extends WebPrimitive with Command {

  override def getSyntax = commandSyntax(List(StringType, StringType, ListType))

  override def perform(args: Array[Argument], context: Context): Unit = carefully {
    if (context.workspace.workspaceContext.workspaceGUI) {
      val dest      = args(0).getString
      val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
      val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
      processResponse(mkRequest(dest, reqMethod, paramMap, Map.empty)) {
        case (response, _) => context.workspace.importDrawing(response)
      }
    }
  }

}
