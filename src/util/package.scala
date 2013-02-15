package org.nlogo.extensions.web

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 2/14/13
 * Time: 3:06 PM
 */

package object util {
  type Streamer = java.io.OutputStream
  def using[A <: { def close() }, B](stream: A)(f: A => B) : B =
    try { f(stream) } finally { stream.close() }
}
