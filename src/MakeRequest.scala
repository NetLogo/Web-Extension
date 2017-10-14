package org.nlogo.extensions.web

import org.nlogo.api.{ Argument, Command, Context, ExtensionException, Reporter }
import org.nlogo.core.Syntax.{ ListType, reporterSyntax, StringType }

object MakeRequest extends WebPrimitive with Reporter {

  override def getSyntax = reporterSyntax(right = List(StringType, StringType, ListType, ListType), ret = ListType)

  override def report(args: Array[Argument], context: Context): AnyRef = carefully {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
    val streamMap =
      paramify(args(3)).getOrElse(Map.empty).mapValues {
        case "export-interface" => Exporter.exportInterface(context)
        case "export-model"     => Exporter.exportModel
        case "export-view"      => Exporter.exportView(context)
        case "export-world"     => Exporter.exportWorld(context)
        case x                  =>
          throw new ExtensionException(s"Unknown export type: '$x'.  Acceptable export types are: " +
                                        "'export-interface', 'export-model', 'export-view', 'export-world'.")
      }
    responseToLogoList(mkRequest(dest, reqMethod, paramMap, streamMap))
  }

}
