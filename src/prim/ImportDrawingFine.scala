package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context }
import org.nlogo.nvm.ExtensionContext
import org.nlogo.window.GUIWorkspace

import util.EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 5:36 PM
 */

object ImportDrawingFine extends WebCommand with CommonWebPrimitive {
  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      ensuringGUIWorkspace(extContext.workspace) { (guiWS: GUIWorkspace) =>
        val (dest, requestMethod, paramMap) = processArguments(args)
        val (response, _) = (new Requester with SimpleWebIntegration)(dest, requestMethod, paramMap)
        guiWS.importDrawing(response)
      }
    }
  }
}
