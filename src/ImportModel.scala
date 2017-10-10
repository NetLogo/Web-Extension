package org.nlogo.extensions.web

import java.io.{ BufferedInputStream, File }
import java.net.URL

import javax.swing.SwingUtilities

import org.nlogo.api.{ Argument, Command, Context, ExtensionException, ModelType }
import org.nlogo.app.App
import org.nlogo.core.Syntax.{ commandSyntax, StringType }

object ImportModel extends WebPrimitive with Command {

  override def getSyntax = commandSyntax(List(StringType))

  override def perform(args: Array[Argument], context: Context): Unit = carefully {
    val dest = args(0).getString
    val path = generateFilePath(dest)
    obtainModelFile(dest, path)
    doLater {
      App.app.fileManager.openFromPath(path, ModelType.Normal)
    }
  }

  private def generateFilePath(url: String): String = {
    val tempDirName    = System.getProperty("java.io.tmpdir")
    val sep            = File.separator
    val modelNameRegex = """[^/]+$""".r
    val modelName      = modelNameRegex.findFirstIn(url).getOrElse(throw new ExtensionException(s"Malformed Model URL: '$url'"))
    s"$tempDirName$sep$modelName"
  }

  private def obtainModelFile(url: String, localPath: String): Unit = {
    using(new BufferedInputStream(new URL(url).openStream())) {
      closeable =>
        val destFile = new File(localPath)
        if (destFile.exists())
          destFile.delete()
        FileWriter(closeable, localPath)
    }
  }

  private def doLater(body: => Unit): Unit = {
    SwingUtilities.invokeLater(new Runnable() {
      override def run(): Unit = { body }
    })
  }

}
