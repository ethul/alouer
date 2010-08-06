/**
 * 
 */
package com.alouer.domain.service.geocoder.impl

import com.alouer.domain.service.geocoder.AbstractGeocoder
import com.alouer.domain.util.{Geolocatable,Geolocation}
import com.alouer.service.util.Logger

/**
 * @author ethul
 *
 */
trait BoundedGeocoder extends AbstractGeocoder {
  private[this] val info = Logger.log(Logger.Info)
  private[this] val warn = Logger.log(Logger.Warning)
  private[this] val bound = 2450
  protected var current = 0
  abstract override protected def lookup(address: String): Geolocatable = {
    info("geocoder is bounded [" + current + " of " + bound + "]")
    if (current < bound) {
      val geo = super.lookup(address)
      current += 1
      geo
    }
    else {
      warn("geocoder has reached its bound [" + current + " of " + bound + "]")
      Geolocation()
    }
  }
}

