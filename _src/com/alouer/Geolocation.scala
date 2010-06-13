/**
 * 
 */
package com.alouer

/**
 * @author ethul
 *
 */
case class Geolocation(latitude: String, longitude: String) {
  val id = strip(latitude) + strip(longitude)
  override def toString(): String = latitude + "," + longitude
  private[this] def strip(value: String) = value.replace("-", "").replace(".", "")
}
