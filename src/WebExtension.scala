package org.nlogo.extensions.web

import org.nlogo.api.{ DefaultClassManager, PrimitiveManager }

import prim._

//@ Add ability to read a file by passed-in Java property
//@ Add ability to directly read file from a stringed URL
//@ Should be able to set property to determine which `WebIntegration` to use for the whole life of the run
class WebExtension extends DefaultClassManager {
  def load(primitiveManager: PrimitiveManager) {
    primitiveManager.addPrimitive("export-world", new ExportWorld())
  }
}

