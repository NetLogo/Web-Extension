package org.nlogo.extensions.web.util

import
  java.{ awt, io },
    awt.image.BufferedImage,
    io.ByteArrayOutputStream

import
  javax.imageio.ImageIO

import
  org.apache.commons.codec.binary.Base64OutputStream

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/23/12
 * Time: 12:54 PM
 */

// Yay!  Typeclasses!
trait ImageToBase64 {
  def asBase64 : String
}

object ImageToBase64 {

  implicit def bufferedImage2String(image: BufferedImage) = new ImageToBase64 {

    private val DefaultByteEncoding = "UTF-8"
    private val DefaultImageFormat  = "png"

    override def asBase64 : String = {
      val os   = new ByteArrayOutputStream()
      val os64 = new Base64OutputStream(os)
      ImageIO.write(image, DefaultImageFormat, os64)
      os.toString(DefaultByteEncoding)
    }

  }

}
