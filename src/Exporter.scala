package org.nlogo.extensions.web

import java.awt.image.BufferedImage
import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, InputStream, PrintWriter, OutputStream, UnsupportedEncodingException }
import java.util.zip.GZIPOutputStream

import javax.imageio.ImageIO

import org.apache.commons.codec.binary.Base64OutputStream

import org.nlogo.api.{ Context, ExtensionException, Workspace }
import org.nlogo.app.{ App, ModelSaver }
import org.nlogo.awt.Images
import org.nlogo.fileformat.basicLoader

object Exporter {

  def exportInterface(context: Context): ByteArrayInputStream =
    NLEvaluator(context.workspace)(()) {
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
          new ByteArrayInputStream(asBase64(Images.paintToImage(component)).getBytes)
    }

  def exportModel: ByteArrayInputStream = {
    val model      = new ModelSaver(App.app, null).currentModelInCurrentVersion
    val modelBytes = basicLoader.sourceString(model, "nlogo").get.getBytes // this may throw an exception if the model couldn't be saved
    new ByteArrayInputStream(modelBytes)
  }

  def exportView(context: Context): ByteArrayInputStream =
    new ByteArrayInputStream(asBase64(context.workspace.exportView).getBytes)

  def exportWorld(context: Context): ByteArrayInputStream = {

    val outputStream = new ByteArrayOutputStream()

    try {
      NLEvaluator(context.workspace)(new GZIPOutputStream(outputStream)) {
        (stream: OutputStream) =>
          val writer = new PrintWriter(stream)
          try context.workspace.exportWorld(writer)
          finally writer.close()
      }
      new ByteArrayInputStream(outputStream.toByteArray)
    } catch {
      case ex: UnsupportedEncodingException =>
        throw new ExtensionException("Unable to convert hooked text to desired encoding", ex)
      case ex: Exception =>
        throw new ExtensionException("Unknown error on hooking/exporting", ex)
    } finally {
      outputStream.close()
    }

  }

  private def asBase64(image: BufferedImage): String = {
    val os   = new ByteArrayOutputStream()
    val os64 = new Base64OutputStream(os)
    ImageIO.write(image, "png", os64)
    os.toString("UTF-8")
  }

}
