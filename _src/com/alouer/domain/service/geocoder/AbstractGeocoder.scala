/**
 * 
 */
package com.alouer.domain.service.geocoder

import com.alouer.domain.util.{Geocache,Geolocatable,Geolocation}
import com.alouer.service.monitor.{Notifiable,Observable,Subscribable}
import com.alouer.service.util.{Logger,Statistics,UrlConnectable}
import scala.collection.mutable.ListBuffer
import scala.xml.{Elem,XML}
import java.net.URLEncoder

/**
 * @author ethul
 *
 */
abstract class AbstractGeocoder(geocache: Geocache[String,Geolocatable])
extends UrlConnectable with Observable {
  private[this] val info = Logger.log(Logger.Info) _
  private[this] val warn = Logger.log(Logger.Warning) _
    
  def encode(address: String): Geolocatable = {
    if (address == null || address == "") {
      warn("address is null or empty, setting to unknown")
      Geolocation()
    }
    else if (geocache.contains(address)) {
      info("cache hit: " + address)
      geocache.get(address).asInstanceOf[Option[Geolocatable]] match {
        case Some(x) => x
        case None => Geolocation()
      }
    }
    else {
      notify(Statistics.GeolookupAccumulator(1))
      val geolocation = lookup(address)
      geocache.put(address, geolocation)
      info("added: " + address + " => " + geolocation)
      geolocation
    }
  }
  
  def encode(addrs: List[String]): List[Geolocatable] = addrs.map(encode _)
  protected def lookup(address: String): Geolocatable
}
