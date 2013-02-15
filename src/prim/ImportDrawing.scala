package org.nlogo.extensions.web.prim

import
  java.net.URL

import
  org.nlogo.{ api, nvm, window },
    api.{ Argument, Context },
    nvm.ExtensionContext,
    window.GUIWorkspace

import
  util.{ EnsuranceAgent, using },
    EnsuranceAgent._

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
        using(new URL(dest).openStream()) {
          guiWS.importDrawing(_)
        }
      }
    }
  }
}
