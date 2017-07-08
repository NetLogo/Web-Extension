package org.nlogo.extensions.web.prim

import
  java.io.{ FileInputStream, InputStream }

import
  org.nlogo.api.{ Argument, Context, ExtensionException }

import 
  org.nlogo.core.{Syntax},
	Syntax._

import
  org.nlogo.extensions.web.requester.{ http, Requester, RequesterGenerator, WebIntegration },
    http.RequestMethod

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 2/27/13
 * Time: 3:47 PM
 */

object UploadFile extends WebReporter with RequesterGenerator {

  override protected type ArgsTuple      = (String, RequestMethod, Map[String, String], String)
  override protected def  primArgsSyntax = List(StringType, StringType, ListType, StringType)
  override protected def  processArguments(args: Array[Argument]) : ArgsTuple = {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)) getOrElse (throw new ExtensionException("Invalid HTTP method name supplied."))
    val params    =      paramify(args(2)) getOrElse defaultMap
    val filePath  = args(3).getString
    (dest, reqMethod, params, filePath)
  }

  override protected type RequesterCons     = (() => InputStream)
  override protected def  generateRequester = (hook: () => InputStream) => new FileExporter(hook) with Integration

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    val (dest, requestMethod, paramMap, filePath) = processArguments(args)
    val hook = () => new FileInputStream(filePath)
    val exporter = generateRequester(hook)
    responseToLogoList(exporter(dest, requestMethod, paramMap))
  }

  protected class FileExporter(hook: () => InputStream) extends Requester {
    self: WebIntegration =>
      override protected def generateAddedExportData = Some(hook())
  }

}
