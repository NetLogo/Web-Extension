package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context, LogoList }
import org.nlogo.nvm.ExtensionContext

import util.EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/22/12
 * Time: 1:17 PM
 */

object ExportView extends WebReporter with CommonWebPrimitive {

  private val DefaultByteEncoding = "UTF-8"
  private val DefaultImageFormat  = "png"

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = {
        () =>
          import java.io.ByteArrayOutputStream, javax.imageio.ImageIO, org.apache.commons.codec.binary.Base64OutputStream
          val image = extContext.workspace.exportView()
          val os    = new ByteArrayOutputStream()
          val os64  = new Base64OutputStream(os)
          ImageIO.write(image, DefaultImageFormat, os64)
          os.toString(DefaultByteEncoding)
      }
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = new ViewExporter(hook) with SimpleWebIntegration
      val (response, statusCode) = exporter(dest, requestMethod, paramMap)
      LogoList(isToString(response), statusCode)
    }
  }

  private class ViewExporter(hook: () => String) extends Requester {
    self: WebIntegration =>
      override protected def generateAddedExportData = Some(hook())
  }

}
