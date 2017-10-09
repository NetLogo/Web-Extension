package org.nlogo.extensions.web.requester

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, InputStream, OutputStream, UnsupportedEncodingException }

import org.nlogo.extensions.web.util.{ EventEvaluator, Streamer }

class StreamerExporter(hook: (OutputStream) => Unit) extends Requester {

  self: WebIntegration with OutStream =>

  override protected def generateAddedExportData: Option[InputStream] = {

    val outputStream = new ByteArrayOutputStream()

    try {
      EventEvaluator(convertStream(outputStream))(hook)
      Option(new ByteArrayInputStream(outputStream.toByteArray))
    } catch {
      case ex: UnsupportedEncodingException =>
        System.err.println(s"Unable to convert hooked text to desired encoding: ${ex.getMessage}\n${ex.getStackTrace}")
        None
      case ex: Exception =>
        System.err.println(s"Unknown error on hooking/exporting: ${ex.getMessage}\n${ex.getStackTrace}")
        None
    } finally {
      outputStream.close()
    }

  }

}

trait OutStream {
  type OStream <: OutputStream
  def convertStream(baos: ByteArrayOutputStream): OStream
}

trait ByteStream extends OutStream {
  override type OStream = ByteArrayOutputStream
  override def convertStream(baos: ByteArrayOutputStream): OStream = baos
}

trait Base64Stream extends OutStream {
  import org.apache.commons.codec.binary.Base64OutputStream
  override type OStream = Base64OutputStream
  override def convertStream(baos: ByteArrayOutputStream): OStream = new Base64OutputStream(baos)
}

trait GZIPStream extends OutStream {
  import java.util.zip.GZIPOutputStream
  override type OStream = GZIPOutputStream
  override def convertStream(baos: ByteArrayOutputStream): OStream = new GZIPOutputStream(baos)
}
