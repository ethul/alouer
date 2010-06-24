/**
 * 
 */
package com.alouer.service.persistence

/**
 * @author ethul
 *
 */
trait Cache[A,B] {
  def contains(key: A): Boolean
  def get(key: A): Option[B]
  def put(key: A, value: B): Boolean
}
