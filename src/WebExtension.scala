package org.nlogo.extensions.web

import org.nlogo.api.{ DefaultClassManager, PrimitiveManager }

import prim.{ ExportWorld, MakeRequest }

class WebExtension extends DefaultClassManager {
  def load(primitiveManager: PrimitiveManager) {
    primitiveManager.addPrimitive("export-world", new ExportWorld())
    primitiveManager.addPrimitive("make-request", new MakeRequest())
  }
}

