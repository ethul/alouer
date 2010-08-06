/**
 * 
 */
package com.alouer.domain.service.geocoder.impl

import com.alouer.domain.service.geocoder.AbstractGeocoder
import com.alouer.domain.util.Geolocatable
import com.alouer.service.util.Logger

/**
 * @author ethul
 *
 */
trait ThrottledGeocoder extends AbstractGeocoder {
  private[this] val info = Logger.log(Logger.Info)
  private[this] val delay = 500
  abstract override protected def lookup(address: String): Geolocatable = {
    info("geocoder lookup being throttled by " + delay + " milliseconds")
    Thread sleep delay
    super.lookup(address)
  }
}