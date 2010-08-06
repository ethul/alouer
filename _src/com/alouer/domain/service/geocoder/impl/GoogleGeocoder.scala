/**
 * 
 */
package com.alouer.domain.service.geocoder.impl

import com.alouer.domain.service.geocoder.AbstractGeocoder
import com.alouer.domain.util.{Geocache,Geolocatable,Geolocation}
import com.alouer.service.monitor.{Notifiable,Observable,Subscribable}
import com.alouer.service.util.{Logger,Statistics}
import scala.xml.{Elem,XML}
import java.net.{URL,URLEncoder}

/**
 * @author ethul
 *
 */
case class GoogleGeocoder(geocache: Geocache[String,Geolocatable]) 
extends AbstractGeocoder(geocache) with Observable {
  private[this] val info = Logger.log(Logger.Info)
  private[this] val warn = Logger.log(Logger.Warning)
  private[this] val error = Logger.log(Logger.Error)
  private[this] val OK = "OK"
  private[this] val encoding = "utf-8"
  private[this] val service = "http://maps.google.com/maps/api/geocode/xml"
  private[this] val addressParam = "address"
  private[this] val sensorParam = "sensor=false"
    
  protected def lookup(address: String): Geolocatable = {
    info("querying geo service for address: " + address)
    val url = service + '?' + addressParam + '=' + format(address) + '&' + sensorParam
    val (xml, status) = try {
      val xml = XML.load(connection(url))
      (xml, (xml \\ "status").text)
    }
    catch {
      case e: Exception => {
        error(e)
        (null, "")
      }
    }
    
    info("status was: " + status)
    
    // lowercase values for a case are taken to be
    // a variable assigned to the match, not as a
    // value. thus we use capital OK
    status match {
      case OK => { 
        val location = xml \\ "location"
        // sometimes google sends us back more than one location in
        // the response, so we just take the first one
        val lat = (location \ "lat")(0).text
        val lng = (location \ "lng")(0).text
        Geolocation(lat, lng)
      } 
      case _ => {
        warn {
          "status " + status + " for " + address + " " +
          "setting to unknown"
        }
        Geolocation()
      }
    }
  }
  
  private[this] def format(address: String) = {
    URLEncoder.encode(address, encoding)
  }
}
