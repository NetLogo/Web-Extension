package org.nlogo.extensions.web

import org.nlogo.api.{ DefaultClassManager, PrimitiveManager }

import prim.{ ExportWorld, ImportWorld, MakeRequest }

class WebExtension extends DefaultClassManager {
  def load(primitiveManager: PrimitiveManager) {
    primitiveManager.addPrimitive("export-world", ExportWorld)
    primitiveManager.addPrimitive("import-world", ImportWorld)
    primitiveManager.addPrimitive("make-request", MakeRequest)
  }
}

