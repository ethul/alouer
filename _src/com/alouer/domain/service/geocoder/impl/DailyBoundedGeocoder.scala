/**
 * 
 */
package com.alouer.domain.service.geocoder.impl

import com.alouer.domain.util.Geolocatable
import com.alouer.service.util.{Logger,TimeAccessor}

/**
 * @author ethul
 *
 */
trait DailyBoundedGeocoder extends BoundedGeocoder {
  private[this] val info = Logger.log(Logger.Info) _
  private[this] val dailyMillis = 24L * 60L * 60L * 1000L
  private[this] var start = 0L
  abstract override protected def lookup(address: String): Geolocatable = {
    if (current == 0) {
      info("started geocoding at " + start)
      start = TimeAccessor.now
    }
    else if (TimeAccessor.now > start + dailyMillis) {
      info("daily bound is over at " + TimeAccessor.now)
      current = 0
    }
    super.lookup(address)
  }
}