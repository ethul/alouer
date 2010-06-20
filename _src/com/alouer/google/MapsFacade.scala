/**
 * 
 */
package com.alouer.google

import com.alouer.util.Logger
import com.alouer.MapMarker
import com.google.gdata.client.maps.MapsService
import com.google.gdata.data.PlainTextConstruct
import com.google.gdata.data.maps.{FeatureFeed,FeatureEntry}
import com.google.gdata.data.maps.{MapFeed,MapEntry}
import com.google.gdata.util.XmlBlob
import scala.collection.mutable.ListBuffer
import java.net.{URL,URLEncoder}

/**
 * @author ethul
 *
 */
class MapsFacade {
  private[this] val infolog = Logger.log(Logger.Info) _
  private[this] val encoding = "utf-8"
  private[this] val application = "alouer"
  private[this] val username = "montreal.alouermap"
  private[this] val password = "bF7nwO@0F"
  private[this] val defaultLat = "45.540323"
  private[this] val defaultLng = "-73.577242"
  private[this] val kml = """
    <Placemark>
      <name>$title</name>
      <description>
        <![CDATA[
        	$address
        	<br />
        	$description
          <br />
          <a href="$link">more details</a>
        ]]>
      </description>
      <Style>
        <IconStyle>
          <Icon>
            <href>http://maps.gstatic.com/intl/en_ALL/mapfiles/ms/micons/blue-dot.png</href>
          </Icon>
        </IconStyle>
      </Style>
      <Point>
        <coordinates>$lng,$lat,0.0</coordinates>
      </Point>
    </Placemark>
  """

  def service(): MapsService = {
    val service = new MapsService(application)
    infolog("getting service for " + username)
    service.setUserCredentials(username, password)
    service
  }
  
  def markup(service: MapsService, markers: Seq[MapMarker]) {
    val feed = new URL("http://maps.google.com/maps/feeds/features/211158563591768037502/000488ebf9fe962fc40ab/full")
    markers.foreach { a => 
      val feature = new FeatureEntry
      val kmlBlob = new XmlBlob

      val lng =
        if (a.geolocation.longitude == "0") {
          defaultLng
        }
        else {
          a.geolocation.longitude
        }

      val lat =
        if (a.geolocation.latitude == "0") {
          defaultLat
        }
        else {
          a.geolocation.latitude
        }

      kmlBlob.setBlob {
        kml.
          replace("$title", URLEncoder.encode(a.item.title, encoding)).
          replace("$address", a.item.address).
          replace("$description", a.item.description).
          replace("$link", a.item.link).
          replace("$lng", lng).
          replace("$lat", lat)
      }
      feature.setKml(kmlBlob)
      infolog("inserting feature with title " + a.item.title)
      infolog("inserting feature with address " + a.item.address)
      service.insert(feed, feature)
    }
  }
  
  def deleteFeatures(service: MapsService) {
    features(service).foreach { a =>
      infolog("deleting " + a.getSelfLink.getHref)
      service.delete(new URL(a.getSelfLink.getHref))
    }
  }
  
  private[this] def features(service: MapsService) = {
    val feed = new URL("http://maps.google.com/maps/feeds/features/211158563591768037502/000488ebf9fe962fc40ab/full")
    val result = service.getFeed(feed, classOf[FeatureFeed])
    val buffer = new ListBuffer[FeatureEntry]
    val entries = result.getEntries
    for (i <- 0 to entries.size-1) {
      buffer += entries.get(i)
    }
    buffer.toList
  }
  
  def listMaps(service: MapsService) {
    val feed = new URL("http://maps.google.com/maps/feeds/maps/default/full")
    val result = service.getFeed(feed, classOf[MapFeed])
    
    println(result.getTitle.getPlainText)
    
    val entries = result.getEntries
    for (i <- 0 to entries.size-1) {
      println(entries.get(i).getId)
      println(entries.get(i).getSelfLink.getHref)
      println(entries.get(i).getSummary.getPlainText)
    }
  }
}
