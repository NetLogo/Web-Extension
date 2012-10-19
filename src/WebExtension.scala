package org.nlogo.extensions.web

import org.nlogo.api.{ DefaultClassManager, PrimitiveManager }

import prim.{ ExportWorld, ImportWorldFine, MakeRequest }

class WebExtension extends DefaultClassManager {
  def load(primitiveManager: PrimitiveManager) {
    primitiveManager.addPrimitive("export-world",      ExportWorld)
    primitiveManager.addPrimitive("import-world-fine", ImportWorldFine)
    primitiveManager.addPrimitive("make-request",      MakeRequest)
  }
}

