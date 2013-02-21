package org.nlogo.extensions.web.requester

import
  java.io.{ ByteArrayInputStream, ByteArrayOutputStream, InputStream, UnsupportedEncodingException }

import
  org.nlogo.extensions.web.util.{ EventEvaluator, Streamer }

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 2/14/13
 * Time: 2:36 PM
 */

class StreamerExporter(hook: (Streamer) => Unit) extends Requester {

  self: WebIntegration with OutStream =>

  override protected def generateAddedExportData : Option[InputStream] = {

    val outputStream = new ByteArrayOutputStream()

    try {
      EventEvaluator(convertStream(outputStream), hook)
      Option(new ByteArrayInputStream(outputStream.toByteArray))
    }
    catch {
      case ex: UnsupportedEncodingException =>
        System.err.println("Unable to convert hooked text to desired encoding: %s\n%s".format(ex.getMessage, ex.getStackTraceString))
        None
      case ex: Exception =>
        System.err.println("Unknown error on hooking/exporting: %s\n%s".format(ex.getMessage, ex.getStackTraceString))
        None
    }
    finally {
      outputStream.close()
    }

  }

}

trait OutStream {
  type OStream <: Streamer
  def convertStream(baos: ByteArrayOutputStream) : OStream
}

trait ByteStream extends OutStream {
  override type OStream = ByteArrayOutputStream
  override def convertStream(baos: ByteArrayOutputStream) : OStream = baos
}

trait Base64Stream extends OutStream {
  import org.apache.commons.codec.binary.Base64OutputStream
  override type OStream = Base64OutputStream
  override def convertStream(baos: ByteArrayOutputStream) : OStream = new Base64OutputStream(baos)
}

trait GZIPStream extends OutStream {
  import java.util.zip.GZIPOutputStream
  override type OStream = GZIPOutputStream
  override def convertStream(baos: ByteArrayOutputStream) : OStream = new GZIPOutputStream(baos)
}
