package org.nlogo.extensions.web

import java.awt.image.BufferedImage
import java.io.{ ByteArrayOutputStream, OutputStream }

import javax.imageio.ImageIO
import javax.swing.SwingUtilities

import scala.concurrent.{ Await, Future }

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

  object EventEvaluator {

    def apply[T, U](stream: T)(hook: (T) => U): U = {
      import scala.concurrent.duration._
      import scala.concurrent.ExecutionContext.Implicits.global
      Await.result(Future { hook(stream) }, 20 seconds)
    }

    // This _cannot_ run within an actor; if it does, NetLogo freezes (for some reason)
    // At least, that's the case with exporting the interface --JAB (10/23/12)
    def withinWorkspace[T, U](workspace: Workspace)(stream: T)(hook: (T) => U): U = {
      workspace.waitForResult[U] (
        new ReporterRunnable[U] {
          override def run(): U = hook(stream)
        }
      )
    }

  }

}
