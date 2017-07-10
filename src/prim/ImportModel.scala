package org.nlogo.extensions.web.prim

import
  java.{ io, net },
    io.{ BufferedInputStream, File },
    net.URL

import
  org.nlogo.{ api, extensions },
    api.{ Argument, Context, ExtensionException, ModelType },
    extensions.web.util.{ FileWriter, doLater, using }

/**
 * Created by IntelliJ IDEA.
 * User: cbrady
 * Date: 5/29/13
 * Time: 1:22 PM
 */
object ImportModel extends WebCommand with SimpleWebPrimitive {

  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    val (dest) = processArguments(args)
    val path   = generateFilePath(dest)
    obtainModelFile(dest, path)
    doLater {
      org.nlogo.app.App.app.fileManager.openFromPath(path, ModelType.Normal)
    }
  }

  private def generateFilePath(url: String) : String = {
    val tempDirName    = System.getProperty("java.io.tmpdir")
    val sep            = File.separator
    val modelNameRegex = """[^/]+$""".r
    val modelName      = modelNameRegex.findFirstIn(url).getOrElse(throw new ExtensionException("Malformed Model URL: '%s'".format(url)))
    tempDirName + sep + modelName
  }

  private def obtainModelFile(url: String, localPath: String) {
    using(new BufferedInputStream(new URL(url).openStream())) {
      closeable =>
        val destFile = new File(localPath)
        if (destFile.exists())
          destFile.delete()
        FileWriter(closeable, localPath)
    }
  }

}
