/**
 * 
 */
package com.alouer.domain.util

/**
 * @author ethul
 *
 */
trait Geolocatable {
  def latitude(): String
  def longitude(): String
  def known(): Boolean
}