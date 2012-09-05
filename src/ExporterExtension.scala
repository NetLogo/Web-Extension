package org.nlogo.extensions.exporter

import java.io.PrintWriter
import java.nio.charset.Charset

import org.nlogo.nvm.ExtensionContext
import org.nlogo.api.{Argument, Context, DefaultClassManager, DefaultCommand, PrimitiveManager, Syntax}
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.cookie.BasicClientCookie

class ExporterExtension extends DefaultClassManager {
  def load(primitiveManager: PrimitiveManager) {
    primitiveManager.addPrimitive("export", new Exporter())
  }
}

class Exporter extends DefaultCommand with StreamHandler {
  override def getSyntax = Syntax.commandSyntax(Array(Syntax.StringType))
  override def getAgentClassString = "O"
  override def perform(args: Array[Argument], context: Context) {
    context match {
      case extContext: ExtensionContext =>
        val exporter = new RemoteExporter(args(0).getString) with WISEIntegration
        exporter{(stream: java.io.OutputStream) =>
          val writer = new PrintWriter(stream)
          try     extContext.workspace.exportWorld(writer)
          finally writer.close()
        }
      case _ => throw new IllegalArgumentException("Context is not an `ExtensionContext`!  (How did you even manage to pull that off?)")
    }
  }
}

private class RemoteExporter(dest: String) extends StreamHandler {

  this: WebIntegration =>

  import java.io.ByteArrayOutputStream

  private val ExportKey = "data"

  def apply(hook: (Streamer) => Unit) {
    val exportText = hookInForText(hook)
//    val myFormat   = """
//{"nodeId":"node_0.jn","visitEndTime":1345775000000,"hintStates":[],"nodeStates":[{"response":"some crap"}],"visitStartTime":1345774000000,"nodeType":"JnlpNode","visitPostTime":null}
//""".format(exportText)
//    val myPostKVs  = Map(ExportKey -> Option(myFormat))
    val myPostKVs = Map(ExportKey -> Option(exportText))
    val allPostKVs = (myPostKVs ++ kvAdditionsMap) collect { case (k, Some(v)) => (k, v) }
    val destOpt = Option(if (!dest.isEmpty) dest else System.getProperty("netlogo.export_destination"))
    val destination = destOpt getOrElse(throw new IllegalStateException("No valid destination given!"))
    HttpHandler.httpPost(allPostKVs, destination, Option(System.getProperty("wise.cookie"))) match { case x => println(x); x }
  }

  protected def hookInForText(hook: (Streamer) => Unit) : String = {

    val outputStream = new ByteArrayOutputStream()
    EventEvaluator(outputStream, hook)

    try     { outputStream.toString("UTF-8") }
    catch   { case ex => System.err.println("An error has occurred in hooking/exporting: " + ex.getMessage); "" }
    finally { outputStream.close() }

  }

}

private[exporter] object HttpHandler {

  import org.apache.http.{ HttpResponse, client, message }
  import message.{ BasicNameValuePair => KVPair }
  import client.entity.{ UrlEncodedFormEntity => URLEntity }, client.methods.HttpPost

  private val DefaultByteEncoding = "UTF-8"

  protected def httpClient = new org.apache.http.impl.client.DefaultHttpClient

  def httpPost(postKVs: Map[String, String], dest: String,
               cookieValue: Option[String] = None, encoding: String = DefaultByteEncoding): String = {

    import collection.JavaConverters.seqAsJavaListConverter

    val post = new HttpPost(new java.net.URL(dest).toURI)
    val javaKVs = (postKVs map { case (key, value) => new KVPair(key, value) } toSeq).asJava
    post.setEntity(new URLEntity(javaKVs, Charset.forName(encoding)))

    // Many, many "official" cookie-insertion approaches were tried; all failed --JAB (9/5/12)
    cookieValue foreach (cookie => post.setHeader("COOKIE", "JSESSIONID=" + cookie))

    readResponse(httpClient.execute(post))

  }

  protected def readResponse(response: HttpResponse) =
    response.getStatusLine + ":\n" + io.Source.fromInputStream(response.getEntity.getContent).mkString.trim

}

object EventEvaluator extends StreamHandler {

  import actors.Actor

  sealed protected trait TempActorProtocol
  protected object TempActorProtocol {
    object Start extends TempActorProtocol
  }

  import TempActorProtocol.Start
  
  protected class EventEvaluationActor[T](stream: T, func: (T) => Unit) extends Actor {
    import org.nlogo.swing.Implicits.thunk2runnable
    def act() {
      loop {
        react {
          case Start => org.nlogo.awt.EventQueue.invokeLater{ () => func(stream); reply() }
        }
      }
    }
  }

  def apply(stream: Streamer, hook: (Streamer) => Unit) {
    ((new EventEvaluationActor(stream, hook)).start() !! Start)()
  }

}

sealed trait StreamHandler {
  protected type Streamer = java.io.OutputStream
}

sealed trait WebIntegration {
  protected def kvAdditionsMap        = Map[String, Option[String]]()
  protected def getProp(prop: String) = Option(System.getProperty(prop))
}

trait WISEIntegration extends WebIntegration {

  private val PeriodIDKey    = "periodId"
  private val RunIDKey       = "runId"
  private val WorkgroupIDKey = "userId"

  private val PeriodIDProp    = "wise.period_id"
  private val RunIDProp       = "wise.run_id"
  private val WorkgroupIDProp = "wise.workgroup_id"

  protected override val kvAdditionsMap = Map(
    PeriodIDKey    -> getProp(PeriodIDProp),
    RunIDKey       -> getProp(RunIDProp),
    WorkgroupIDKey -> getProp(WorkgroupIDProp)
  )

}

