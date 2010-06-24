/**
 * 
 */
package com.alouer.domain.util

import com.alouer.service.monitor.{Notifiable,Observable,Subscribable}
import com.alouer.service.util.{Logger,Statistics}
import scala.collection.mutable.ListBuffer
import scala.xml.{Elem,XML}
import java.net.{URL,URLEncoder}

/**
 * @author ethul
 *
 */
case class Geocoder(geocache: Geocache[String,Geolocatable]) extends Observable {
  private[this] val info = Logger.log(Logger.Info) _
  private[this] val warn = Logger.log(Logger.Warning) _
  private[this] val service = "http://maps.google.com/maps/api/geocode/xml"
  private[this] val addressParam = "address"
  private[this] val sensorParam = "sensor=false"
  private[this] val ok = "OK"
  private[this] val encoding = "utf-8"
  private[this] val delayMillis = 500
  protected val subscribers = ListBuffer[Subscribable]()
    
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
      // TODO: ethul, figure out a better way to delay
      Thread.sleep(delayMillis)
      val geolocation = lookup(address)
      geocache.put(address, geolocation)
      info("added: " + address + " => " + geolocation)
      geolocation
    }
  }
  
  def encode(addrs: List[String]): List[Geolocatable] = addrs.map(encode _)
  
  private[this] def lookup(address: String): Geolocatable = {
    val url = new URL(service + '?' + addressParam + '=' + format(address) + '&' + sensorParam)
    info("querying geo service for address: " + address)
    val xml = XML.load(url.openStream)
    val status = (xml \\ "status").text
    info("status was: " + status)
    
    if (status == ok) {
      val location = xml \\ "location"
      // sometimes google sends us back more than one location in
      // the response, so we just take the first one
      val lat = (location \ "lat")(0).text
      val lng = (location \ "lng")(0).text
      Geolocation(lat, lng)
    }
    else {
      warn {
        "status " + status + " for " + address + " " +
        "setting to unknown"
      }
      Geolocation()
    }
  }
  
  private[this] def format(address: String) = {
    URLEncoder.encode(address, encoding)
  }
}
