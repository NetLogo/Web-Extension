package org.nlogo.extensions.web

import java.awt.image.BufferedImage
import java.io.{ ByteArrayOutputStream, OutputStream }

import javax.imageio.ImageIO
import javax.swing.SwingUtilities

import org.apache.commons.codec.binary.Base64OutputStream

import org.nlogo.api.{ ReporterRunnable, Workspace }

package object util {

  def using[A <: { def close() }, B](stream: A)(f: A => B): B =
    try { f(stream) } finally { stream.close() }

  def doLater(body: => Unit): Unit = {
    SwingUtilities.invokeLater(new Runnable() {
      override def run(): Unit = { body }
    })
  }

  object AsBase64 {
    def apply(image: BufferedImage): String = {
      val os   = new ByteArrayOutputStream()
      val os64 = new Base64OutputStream(os)
      ImageIO.write(image, "png", os64)
      os.toString("UTF-8")
    }
  }

  def nlEvaluate[T, U](workspace: Workspace)(stream: T)(hook: (T) => U): U = {
    workspace.waitForResult[U] (
      new ReporterRunnable[U] {
        override def run(): U = hook(stream)
      }
    )
  }

}
