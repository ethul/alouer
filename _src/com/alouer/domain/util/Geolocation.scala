/**
 * 
 */
package com.alouer.domain.util

/**
 * @author ethul
 *
 */
case class Geolocation(latitude: String, longitude: String, known: Boolean) extends Geolocatable
object Geolocation {
  private[this] val unknown = "0"
  def apply() = new Geolocation(unknown, unknown, false)
  def apply(latitude: String, longitude: String) = new Geolocation(latitude, longitude, true)
}