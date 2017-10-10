package org.nlogo.extensions.web

import org.nlogo.api.ExtensionException

import java.io.{ File, FileOutputStream, InputStream, IOException }

object FileWriter {

  private val DefaultBufferSize = 1024

  def apply(is: InputStream, filepath: String, filename: String = ""): Unit = {

    val truePath =
      if (new File(filepath).isDirectory)
        s"$filepath${File.separator}$filename"
      else
        filepath

    val file = new File(truePath)

    val fileWasMade = {
      try file.createNewFile()
      catch {
        case ex: IOException => throw new ExtensionException(s"Failed to write file: $truePath", ex)
      }
    }

    if (fileWasMade) {

      val fos = new FileOutputStream(file, true)

      try {
        val buff = new Array[Byte](DefaultBufferSize)
        var n    = is.read(buff)
        while (n != -1) {
          fos.write(buff, 0, n)
          n = is.read(buff)
        }
      } catch {
        case ex: IOException => throw new ExtensionException(s"Failed to write file: $truePath", ex)
      } finally {
        fos.close()
      }

    } else {
      throw new ExtensionException(s"File '$truePath' already exists; please delete it or choose a different filename.")
    }

  }

}
