package org.nlogo.extensions.web

import
  org.nlogo.api.{ DefaultClassManager, PrimitiveManager }

import
  prim._

class WebExtension extends DefaultClassManager {
  def load(primitiveManager: PrimitiveManager) {
    primitiveManager.addPrimitive("download-file",       DownloadFile)
    primitiveManager.addPrimitive("download-file-fine",  DownloadFileFine)
    primitiveManager.addPrimitive("export-interface",    ExportInterface)
    primitiveManager.addPrimitive("export-model",        ExportModel)
    primitiveManager.addPrimitive("export-view",         ExportView)
    primitiveManager.addPrimitive("export-world",        ExportWorld)
    primitiveManager.addPrimitive("import-drawing",      ImportDrawing)
    primitiveManager.addPrimitive("import-drawing-fine", ImportDrawingFine)
    primitiveManager.addPrimitive("import-model",        ImportModel)
    primitiveManager.addPrimitive("import-world",        ImportWorld)
    primitiveManager.addPrimitive("import-world-fine",   ImportWorldFine)
    primitiveManager.addPrimitive("make-request",        MakeRequest)
    primitiveManager.addPrimitive("upload-file",         UploadFile)
  }
}

