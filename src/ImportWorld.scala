package org.nlogo.extensions.web

import java.io.{ ByteArrayInputStream, InputStream, InputStreamReader }
import java.util.zip.GZIPInputStream

import scala.io.{ Codec, Source }

import org.nlogo.api.{ Argument, Command, Context, ExtensionException }
import org.nlogo.core.Syntax.{ commandSyntax, StringType }
import org.nlogo.nvm.ExtensionContext

import org.nlogo.extensions.web.requester.{ NLEvaluator, Requester, SimpleWebIntegration }

object ImportWorld extends WebPrimitive with Command {

  override def getSyntax = commandSyntax(List(StringType))

  override def perform(args: Array[Argument], context: Context): Unit = carefully {

    val dest  = args(0).getString
    val bytes = Source.fromURL(dest)(Codec.ISO8859).map(_.toByte).toArray
    val bais  = new ByteArrayInputStream(bytes)

    NLEvaluator(context.workspace)(bais) {
      (stream: InputStream) =>
        val gis = new GZIPInputStream(stream)
        context.workspace.importWorld(new InputStreamReader(gis))
        stream.close()
    }

  }

}

object ImportWorldFine extends WebPrimitive with Command {

  override def getSyntax = commandSyntax(List(StringType))

  override def perform(args: Array[Argument], context: Context): Unit = {

    val dest          = args(0).getString
    val reqMethod     = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap      = paramify     (args(2)).getOrElse(Map.empty)
    val (response, _) = (new Requester with SimpleWebIntegration)(dest, reqMethod, paramMap)

    NLEvaluator(context.workspace)(response) {
      (stream: InputStream) =>
        val gis = new GZIPInputStream(stream)
        context.workspace.importWorld(new InputStreamReader(gis))
        stream.close()
    }

  }

}
