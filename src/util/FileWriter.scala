package org.nlogo.extensions.web.util

import org.nlogo.api.ExtensionException

import java.io.{ File, FileOutputStream, InputStream, IOException }

object FileWriter {

  private val DefaultBufferSize = 1024

  def apply(is: InputStream, filepath: String, filename: String = ""): Unit = {

    val truePath = if (new File(filepath).isDirectory) filepath + File.separator + filename else filepath
    val file     = new File(truePath)

    val fileWasMade = {
      try { file.createNewFile() }
      catch {
        case ex: IOException => throw new ExtensionException("Failed to create file: " + truePath, ex)
      }
    }

    if (fileWasMade) {
      using(new FileOutputStream(file, true)) { fos =>
        try {
          val buff = new Array[Byte](DefaultBufferSize)
          var n    = is.read(buff)
          while (n != -1) {
            fos.write(buff, 0, n)
            n = is.read(buff)
          }
        } catch {
          case ex: IOException => throw new ExtensionException("Failed to write file: " + truePath, ex)
        }
      }
    }
    else throw new ExtensionException(s"File '$truePath' already exists; please delete it or choose a different filename.")

  }

}
