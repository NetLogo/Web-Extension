package org.nlogo.extensions.web

import java.io.{ ByteArrayInputStream, InputStream }

import org.nlogo.api.{ Argument, Context, ExtensionException, Reporter, Workspace }
import org.nlogo.app.App
import org.nlogo.awt.Images
import org.nlogo.core.Syntax.{ ListType, reporterSyntax, StringType }
import org.nlogo.nvm.ExtensionContext

import org.nlogo.extensions.web.requester.{ NLEvaluator, Requester, SimpleWebIntegration }

object ExportInterface extends WebPrimitive with Reporter {

  override def getSyntax = reporterSyntax(right = List(StringType, StringType, ListType), ret = ListType)

  override def report(args: Array[Argument], context: Context): AnyRef = carefully {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
    val exporter  =
      new Requester with SimpleWebIntegration {
        override protected def generateAddedExportData = {
          Option(NLEvaluator(context.workspace)(()) {
            (_: Unit) =>
              /*
               Yikes!  I didn't want to have to do this; I originally added a method to `Workspace` to
               extract a `BufferedImage` in much the same way that `export-interface` files are written out,
               but, when the view isn't blank (and sometimes when it is), NetLogo will just freeze in some
               Java standard library code when exporting the interface.  Instead, we have to do things in
               such a way here that we ask the workspace to generate this image when it feels up to the task.
               Now that I think of it... why don't we need to do that when exporting the view...? --JAB (10/23/12)
              */
              val component = App.app.tabs.interfaceTab.getInterfacePanel
              new ByteArrayInputStream(AsBase64(Images.paintToImage(component)).getBytes)
          })
        }
      }
    responseToLogoList(exporter(dest, reqMethod, paramMap))
  }

}
