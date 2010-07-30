/**
 * 
 */
package com.alouer.service.persistence

import com.alouer.service.util.Logger

/**
 * @author ethul
 *
 */
abstract class Configuration[A,B] 
extends Cache[A,B] 
with Reloadable

object Configuration extends Configuration[String,String] {
  private var cache: Cache[String,String] = _
  
  def load(file: String, separator: String) {
    cache = FileCache[String,String](file, separator)
  }
  
  def get(key: String): Option[String] = cache.get(key) match {
    case Some(x) => Some(x)
    case None => {
      val message = "configuration not found for " + key
      
      try {
        Logger.log(Logger.Error) {
          message
        }
      }
      catch {
        case e => println(e)
      }
      
      throw new RuntimeException(message)
    }
  }
  
  def contains(key: String): Boolean = throw new UnsupportedOperationException
  def put(key: String, value: String) = throw new UnsupportedOperationException
  def reload(): Boolean = throw new UnsupportedOperationException
}
