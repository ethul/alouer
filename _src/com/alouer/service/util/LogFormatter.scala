/**
 * 
 */
package com.alouer.service.util

import java.io.{ByteArrayOutputStream,PrintStream}

/**
 * this class will be the type class implemented for each supported
 * type below in the companion object
 * 
 * @author ethul
 */
abstract class LogFormatter[A] {
  def format(a: A): String
}

/**
 * the companion object defines all implementations of the type 
 * class above.
 * 
 * <p>
 * each implementation is an implicit object which will be
 * chosen accordingly at compile-time based on the parameterized
 * type A of the LogFormatter, which is obtained from some client
 * object which has a method invoked using the type class.
 *  
 * @author ethul
 */
object LogFormatter {
  implicit object StringLogFormatter extends LogFormatter[String] {
    def format(s: String) = s
  }
  
  implicit object ExceptionLogFormatter extends LogFormatter[Exception] {
    def format(e: Exception) = {
      val buffer = new ByteArrayOutputStream
      e.printStackTrace(new PrintStream(buffer))
      buffer.toString
    }
  }
}