package org.nlogo.extensions.web.prim.util

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/17/12
 * Time: 1:10 PM
 */

trait StreamHandler {
  protected type Streamer = java.io.OutputStream
  protected def using[A <: { def close() }, B](stream: A)(f: A => B) : B =
    try { f(stream) } finally { stream.close() }
}
