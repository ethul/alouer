/**
 * 
 */
package com.alouer.domain.util

import com.alouer.service.persistence.Cache

/**
 * @author ethul
 *
 */
case class Geocache[A <: String, B <: Geolocatable](cache: Cache[A,A]) extends Cache[A,B] {
  def contains(key: A) = cache.contains(key)
  def put(key: A, value: B) = {
    val formatted = value.latitude + "," + value.longitude
    cache.put(key, formatted.asInstanceOf[A])
  }
  def get(key: A) = {
    val value = cache.get(key)
    val locations = value.get.split(',')
    new Some(Geolocation(locations(0), locations(1)).asInstanceOf[B])
  }
}
