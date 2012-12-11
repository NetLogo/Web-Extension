package org.nlogo.extensions.web.prim.util

import org.nlogo.api.ExtensionException

import java.io.{ File, FileOutputStream, InputStream }

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 12/11/12
 * Time: 4:06 PM
 */

object FileWriter {

  private val DefaultBufferSize = 1024

  def apply(is: InputStream, filepath: String, filename: String) {
    val truePath = if (new File(filepath).isDirectory) filepath + File.separator + filename else filepath
    val file     = new File(truePath)
    if (file.createNewFile()) {
      val fos  = new FileOutputStream(file, true)
      val buff = new Array[Byte](DefaultBufferSize)
      var n    = is.read(buff)
      while (n != -1) {
        fos.write(buff, 0, n)
        n = is.read(buff)
      }
      fos.close()
    }
    else throw new ExtensionException("File '%s' already exists; please delete it or choose a different filename.".format(truePath))
  }

}
