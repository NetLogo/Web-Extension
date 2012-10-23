package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context, LogoList }
import org.nlogo.app.App
import org.nlogo.awt.Images
import org.nlogo.nvm.{ ExtensionContext, Workspace }

import util.EnsuranceAgent._
import util.EventEvaluator

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/22/12
 * Time: 3:05 PM
 */

object ExportInterface extends WebReporter with CommonWebPrimitive {

  private val DefaultByteEncoding = "UTF-8"
  private val DefaultImageFormat  = "png"

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = {
        (_: Unit) =>
          import java.io.ByteArrayOutputStream, javax.imageio.ImageIO, org.apache.commons.codec.binary.Base64OutputStream
          /*
           Yikes!  I didn't want to have to do this; I originally added a method to `Workspace` to
           extract a `BufferedImage` in much the same way that `export-interface` files are written out,
           but, when the view isn't blank (and sometimes when it is), NetLogo will just freeze in some
           Java standard library code when exporting the interface.  Instead, we have to do things in
           such a way here that we ask the workspace to generate this image when it feels up to the task.
           Now that I think of it... why don't we need to do that when exporting the view...? --JAB (10/23/12)
          */
          val comp  = App.app.tabs.interfaceTab.getInterfacePanel
          val image = Images.paintToImage(comp)
          val os    = new ByteArrayOutputStream()
          val os64  = new Base64OutputStream(os)
          ImageIO.write(image, DefaultImageFormat, os64)
          os.toString(DefaultByteEncoding)
      }
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = new ViewExporter(hook, extContext.workspace) with SimpleWebIntegration
      val (response, statusCode) = exporter(dest, requestMethod, paramMap)
      LogoList(isToString(response), statusCode)
    }
  }

  private class ViewExporter(hook: (Unit) => String, workspace: Workspace) extends Requester {
    self: WebIntegration =>
      override protected def generateAddedExportData = Option(EventEvaluator.withinWorkspace((), hook, workspace))
  }

}
