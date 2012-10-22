package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context }
import org.nlogo.nvm.ExtensionContext
import org.nlogo.window.GUIWorkspace

import java.net.URL

import util.EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 5:04 PM
 */

object ImportDrawing extends WebCommand with SimpleWebPrimitive {
  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      ensuringGUIWorkspace(extContext.workspace) { (guiWS: GUIWorkspace) =>
        val (dest) = processArguments(args)
        val is     = new URL(dest).openStream()
        guiWS.importDrawing(is)
      }
    }
  }
}
