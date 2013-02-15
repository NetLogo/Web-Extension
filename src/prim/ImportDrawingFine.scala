package org.nlogo.extensions.web.prim

import
  org.nlogo.{ api, nvm, window },
    api.{ Argument, Context },
    nvm.ExtensionContext,
    window.GUIWorkspace

import
  org.nlogo.extensions.web.{ requester, util },
    requester.SimpleRequesterGenerator,
    util.EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 5:36 PM
 */

object ImportDrawingFine extends WebCommand with CommonWebPrimitive with SimpleRequesterGenerator {
  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      ensuringGUIWorkspace(extContext.workspace) { (guiWS: GUIWorkspace) =>
        val (dest, requestMethod, paramMap) = processArguments(args)
        processResponse(generateRequester()(dest, requestMethod, paramMap)) {
          case (response, _) => guiWS.importDrawing(response)
        }
      }
    }
  }
}
