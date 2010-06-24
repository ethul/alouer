/**
 * 
 */
package com.alouer.service.persistence

/**
 * @author ethul
 *
 */
trait Reloadable {
  def reload(): Boolean
}