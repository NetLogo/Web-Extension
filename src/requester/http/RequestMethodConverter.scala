package org.nlogo.extensions.web.requester.http

import java.io.InputStream
import java.net.URI
import java.nio.charset.Charset

import org.apache.http.client.methods._
import org.apache.http.entity.mime.{ HttpMultipartMode, MultipartEntity }
import org.apache.http.entity.mime.content.{ InputStreamBody, StringBody }

// Some traits for enforcing how a request handles its additional parameters
sealed trait ParamHandler {
  def handleParams(paramMap: Map[String, String], encoding: String, lazyMap: Map[String, InputStream] = Map()): Unit
}

private trait URLParams extends ParamHandler {

  self: HttpRequestBase =>

  override def handleParams(paramMap: Map[String, String], encoding: String, lazyMap: Map[String, InputStream] = Map()): Unit = {
    val encode   = java.net.URLEncoder.encode(_: String, encoding)
    val lazies   = lazyMap mapValues {
      is =>
        val scanner  = new java.util.Scanner(is).useDelimiter("\\A")
        if (scanner.hasNext) scanner.next else ""
    }
    val queryStr = (paramMap ++ lazies) map { case (k, v) => s"${encode(k)}=${encode(v)}" } mkString ("?", "&", "")
    setURI(new URI(getURI.toString + queryStr))
  }

}

private trait EntityParams extends ParamHandler {

  self: HttpEntityEnclosingRequestBase =>

  override def handleParams(paramMap: Map[String, String], encoding: String, lazyMap: Map[String, InputStream] = Map()): Unit = {
    val entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)
    paramMap foreach { case (k, v) => entity.addPart(k, new StringBody(v, "text/plain", Charset.forName("UTF-8"))) }
    lazyMap  foreach { case (k, v) => entity.addPart(k, new InputStreamBody(v, k)) }
    setEntity(entity)
  }

}



sealed trait RequestMethodConverter[T] {
  def apply(method: RequestMethod): T
}

object ToApacheConverter extends RequestMethodConverter[HttpRequestBase with ParamHandler] {
  def apply(method: RequestMethod): HttpRequestBase with ParamHandler = method match {
    case Delete  => new HttpDelete  with URLParams
    case Get     => new HttpGet     with URLParams
    case Head    => new HttpHead    with URLParams
    case Options => new HttpOptions with URLParams
    case Patch   => new HttpPatch   with EntityParams
    case Post    => new HttpPost    with EntityParams
    case Put     => new HttpPut     with EntityParams
    case Trace   => new HttpTrace   with URLParams
  }
}
