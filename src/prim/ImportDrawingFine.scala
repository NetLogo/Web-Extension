package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context }
import org.nlogo.nvm.ExtensionContext
import org.nlogo.window.GUIWorkspace

import java.io.ByteArrayInputStream

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 5:36 PM
 */

object ImportDrawingFine extends WebCommand with CommonWebPrimitive {
  def perform(args: Array[Argument], context: Context) {
    context match {
      case extContext: ExtensionContext =>
        extContext.workspace match {
          case guiWS: GUIWorkspace =>
            val (dest, requestMethod, paramMap) = processArguments(args)
            val (response, _) = (new Requester with SimpleWebIntegration)(dest, requestMethod, paramMap)
            guiWS.importDrawing(response)
          case ws => throw new UnsupportedOperationException(
            "Cannot use this primitive from any type of workspace by a `GUIWorkspace`; you're using a %s.".format(ws.getClass.getName))
        }
      case _ => throw new IllegalArgumentException("Context is not an `ExtensionContext`!  (How did you even manage to pull that off?)")
    }
  }
}
