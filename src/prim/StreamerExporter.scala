package org.nlogo.extensions.web.prim

import
  java.io.{ ByteArrayOutputStream, UnsupportedEncodingException }

import
  requester.{ Requester, WebIntegration },
  util.{ EventEvaluator, Streamer }

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 2/14/13
 * Time: 2:36 PM
 */

class StreamerExporter(hook: (Streamer) => Unit) extends Requester {

  self: WebIntegration with OutStream =>

  private val DefaultByteEncoding = "UTF-8"

  override protected def generateAddedExportData = {

    val outputStream = new ByteArrayOutputStream()

    try {
      EventEvaluator(convertStream(outputStream), hook)
      Option(outputStream.toString(DefaultByteEncoding))
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
