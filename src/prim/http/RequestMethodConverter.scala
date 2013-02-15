package org.nlogo.extensions.web.prim.http

import
  java.{ net, nio },
    net.URI,
    nio.charset.Charset

import
  org.apache.http.{ client, message },
    client.{ entity, methods },
      entity.{ UrlEncodedFormEntity => URLEntity },
      methods._,
    message.{ BasicNameValuePair => KVPair }

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/18/12
 * Time: 2:50 PM
 */

// Some traits for enforcing how a request handles its additional parameters
sealed trait ParamHandler {
  def handleParams(paramMap: Map[String, String], encoding: String)
}

private trait URLParams extends ParamHandler {

  self: HttpRequestBase =>

  override def handleParams(paramMap: Map[String, String], encoding: String) {
    val encode   = java.net.URLEncoder.encode(_: String, encoding)
    val queryStr = paramMap map { case (k, v) => "%s=%s".format(encode(k), encode(v)) } mkString ("?", "&", "")
    setURI(new URI(getURI.toString + queryStr))
  }

}

private trait EntityParams extends ParamHandler {

  self: HttpEntityEnclosingRequestBase =>

  override def handleParams(paramMap: Map[String, String], encoding: String) {
    import collection.JavaConverters._
    val javaKVs = (paramMap map { case (key, value) => new KVPair(key, value) } toSeq).asJava
    setEntity(new URLEntity(javaKVs, Charset.forName(encoding)))
  }

}



sealed trait RequestMethodConverter[T] {
  def apply(method: RequestMethod) : T
}

object ToApacheConverter extends RequestMethodConverter[HttpRequestBase with ParamHandler] {
  def apply(method: RequestMethod) : HttpRequestBase with ParamHandler = method match {
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

