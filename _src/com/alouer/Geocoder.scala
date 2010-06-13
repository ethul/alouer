/**
 * 
 */
package com.alouer

import com.alouer.util.Logger
import scala.xml.{Elem,XML}
import java.net.{URL,URLEncoder}

/**
 * @author ethul
 *
 */
class Geocoder(geocache: Geocache) {
  private[this] val infolog = Logger.log(Logger.Info) _
  private[this] val warninglog = Logger.log(Logger.Warning) _
  private[this] val service = "http://maps.google.com/maps/api/geocode/xml"
  private[this] val addressParam = "address"
  private[this] val sensorParam = "sensor=false"
  private[this] val ok = "OK"
  private[this] val encoding = "utf-8"
   
  def encode(address: String): Geolocation = {
    if (address == null || address == "") {
      warninglog("address is null or empty")
      Geolocation("0","0")
    }
    else if (geocache.contains(address)) {
      infolog("cache hit: " + address)
      geocache.get(address).get
    }
    else {
      val geolocation = lookup(address)
      geocache.put(address, geolocation)
      infolog("added: " + address + " => " + geolocation)
      geolocation
    }
  }
  
  private[this] def lookup(address: String): Geolocation = {
    val url = new URL(service + '?' + addressParam + '=' + format(address) + '&' + sensorParam)
    infolog("querying geo service for address: " + address)
    val xml = XML.load(url.openStream)
    val status = (xml \\ "status").text
    infolog("status was: " + status)
    
    if (status == ok) {
      val location = xml \\ "location"
      // somestimes google sends us back more than one location in
      // the response, so we just take the first one
      val lat = (location \ "lat")(0).text
      val lng = (location \ "lng")(0).text
      Geolocation(lat, lng)
    }
    else {
      warninglog("status " + status + " for " + address)
      throw new IllegalArgumentException()
    }
  }
  
  private[this] def format(address: String) = {
    URLEncoder.encode(address, encoding)
  }
}
