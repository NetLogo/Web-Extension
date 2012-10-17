package org.nlogo.extensions.exporter

import java.net.URL
import java.nio.charset.Charset

import org.apache.http.{ HttpResponse, client, impl, message }
import impl.client.DefaultHttpClient
import message.{ BasicNameValuePair => KVPair }
import client.entity.{ UrlEncodedFormEntity => URLEntity }, client.methods.HttpPost

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/17/12
 * Time: 1:00 PM
 */

object HttpService {

  private val DefaultByteEncoding = "UTF-8"

  protected def generateClient = new DefaultHttpClient

  def post(paramMap: Map[String, String], dest: String, cookieValue: Option[String] = None,
           encoding: String = DefaultByteEncoding) : String = {

    import collection.JavaConverters._

    val client = generateClient

    val post = new HttpPost(new URL(dest).toURI)
    val javaKVs = (paramMap map { case (key, value) => new KVPair(key, value) } toSeq).asJava
    post.setEntity(new URLEntity(javaKVs, Charset.forName(encoding)))

    // Many, many "official" cookie-insertion approaches were tried; all failed --JAB (9/5/12)
    cookieValue foreach (cookie => post.setHeader("COOKIE", "JSESSIONID=" + cookie))

    readResponse(client.execute(post))

  }

  protected def readResponse(response: HttpResponse) =
    response.getStatusLine + ":\n" + io.Source.fromInputStream(response.getEntity.getContent).mkString.trim

}
