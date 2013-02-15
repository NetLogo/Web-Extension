package org.nlogo.extensions.web.prim.util

import
  org.nlogo.{ api, nvm, window },
    api.Context,
    nvm.{ ExtensionContext, Workspace },
    window.GUIWorkspace

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/22/12
 * Time: 1:46 PM
 */

// Type erasure, you scurvy dog, you almost tricked me into trying to make one single generic method here! --JAB (10/22/12)
object EnsuranceAgent {

  def ensuringExtensionContext[T](f: (ExtensionContext) => T)(implicit context: Context) : T = {
    context match {
      case extContext: ExtensionContext => f(extContext)
      case _ => throw new IllegalArgumentException("Context is not an `ExtensionContext`!  (How did you even manage to pull that off?)")
    }
  }

  def ensuringGUIWorkspace[T](ws: Workspace)(f: (GUIWorkspace) => T) : T = {
    ws match {
      case guiWS: GUIWorkspace => f(guiWS)
      case other => throw new UnsupportedOperationException(
        "Cannot use this primitive from any type of workspace by a `GUIWorkspace`; you're using a %s.".format(other.getClass.getName))
    }
  }

}
