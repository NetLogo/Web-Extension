package org.nlogo.extensions.web

import java.io.{ BufferedInputStream, File }
import java.net.URL

import org.nlogo.api.{ Argument, Command, Context, ExtensionException }
import org.nlogo.core.Syntax.{ commandSyntax, ListType, StringType }

import org.nlogo.extensions.web.http.RequestMethod

object DownloadFile extends WebPrimitive with Command {

  override def getSyntax = commandSyntax(List(StringType, StringType))

  override def perform(args: Array[Argument], context: Context): Unit = carefully {
    val dest     = args(0).getString
    val filepath = args(1).getString
    val filename = new File(filepath).getName
    using(new BufferedInputStream(new URL(dest).openStream())) {
      FileWriter(_, filepath, filename)
    }
  }

}

object DownloadFileFine extends WebPrimitive with Command {

  override def getSyntax = commandSyntax(List(StringType, StringType, ListType, StringType))

  override def perform(args: Array[Argument], context: Context): Unit = carefully {

    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
    val filepath  = args(3).getString
    val filename  = new File(filepath).getName

    processResponse(mkRequest(dest, reqMethod, paramMap, Map.empty)) {
      case (response, _) => FileWriter(response, filepath, filename)
    }

  }

}
