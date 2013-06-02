package org.nlogo.extensions.web.prim

import
  java.net.URL

import
  org.nlogo.{ api, nvm, window },
    api.{ModelType, ExtensionException, Argument, Context},
      nvm.ExtensionContext,
      window.GUIWorkspace

import
  org.nlogo.extensions.web.util.{FileWriter, EnsuranceAgent, using},
    EnsuranceAgent._

import
  java.io.BufferedInputStream

/**
 * Created by IntelliJ IDEA.
 * User: cbrady
 * Date: 5/29/13
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
object ImportModel extends WebCommand with SimpleWebPrimitive {

  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      ensuringGUIWorkspace(extContext.workspace) { (guiWS: GUIWorkspace) =>
        val (dest)         = processArguments(args)
        val modelNameRegex = """[^/]+$""".r
        val modelNameMatch = modelNameRegex.findFirstMatchIn( dest ).getOrElse( throw new ExtensionException("Malformed Model URL: " + dest) )
        val modelName      = modelNameMatch.matched
        val tempDirName    = System.getProperty("java.io.tmpdir")
        using(new BufferedInputStream( new URL(dest).openStream() )) {
          closeable =>
            val destFile = new java.io.File(tempDirName + modelName)
            if (destFile.exists()) destFile.delete()
            FileWriter(closeable, tempDirName, modelName)
        }
        invokeLater{ {
          org.nlogo.app.App.app.fileMenu.openFromPath(tempDirName + modelName, ModelType.Normal)
        } }
      }
    }
  }

  def invokeLater(body: => Unit) {
   javax.swing.SwingUtilities.invokeLater(new Runnable() {
       override def run() { body }
   })
  }

}
