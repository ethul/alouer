/**
 * 
 */
package com.alouer.domain.presentation

import com.alouer.domain.util.{Geolocation,Geopolygon}
import com.alouer.service.util.Logger
import com.google.gdata.client.maps.MapsService
import com.google.gdata.data.PlainTextConstruct
import com.google.gdata.data.maps.{FeatureFeed,FeatureEntry}
import com.google.gdata.data.maps.{MapFeed,MapEntry}
import com.google.gdata.util.XmlBlob
import scala.collection.mutable.ListBuffer
import scala.xml.XML
import java.net.{URL,URLEncoder}

/**
 * @author ethul
 *
 */
case class GoogleMaps(username: String, password: String) {
  private[this] val infolog = Logger.log(Logger.Info) _
  private[this] val error = Logger.log(Logger.Error) _
  private[this] val encoding = "utf-8"
  private[this] val application = "alouer"
  private[this] val defaultLat = "45.50706872936989"
  private[this] val defaultLng = "-73.55587005615234"
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

  infolog("getting service for " + username)
  private[this] val service = try {
    val service = new MapsService(application)
    service.setUserCredentials(username, password)
    service
  }
  catch {
    case e:Exception => {
      error(e)
      null
    }
  }
  
  def createFeatures(features: List[MapFeature]) {
    val feed = new URL("http://maps.google.com/maps/feeds/features/211158563591768037502/000488ebf9fe962fc40ab/full")
    features.foreach { a => 
      val feature = new FeatureEntry
      val kmlBlob = new XmlBlob

      val lng =
        if (a.known) {
          a.longitude
        }
        else {
          defaultLng
        }

      val lat =
        if (a.known) {
          a.latitude
        }
        else {
          defaultLat
        }

      kmlBlob.setBlob {
        kml.
          replace("$title", URLEncoder.encode(a.title, encoding)).
          replace("$address", a.address).
          replace("$description", a.description).
          replace("$link", a.link).
          replace("$lng", lng).
          replace("$lat", lat)
      }
      feature.setKml(kmlBlob)
      infolog { 
        "inserting feature with title " + a.title +
        "inserting feature with address " + a.address
      }

      try {
        service.insert(feed, feature)
      }
      catch {
        case e: Exception => {
          error(e)
          infolog("unable to insert feature: " + a)
        }
      }
    }
  }
  
  def deleteFeatures() {
    features.foreach { a =>
      infolog("deleting " + a.getSelfLink.getHref)
      service.delete(new URL(a.getSelfLink.getHref))
    }
  }

  def getFeaturePolygons(): List[Geopolygon] = {
    features.map { a =>
      val kml = a.getKml.getBlob
      val xml = XML.loadString(kml)
      val polygon = xml \\ "Polygon"
      if (!polygon.isEmpty) {
        val coordinates = polygon \\ "coordinates"
        val list = coordinates.text.split(" ")
        val geos = list.map { a =>
          val b = a.split(",")
          val (lng,lat,z) = (b(0),b(1),b(2))
          Geolocation(lat,lng)
        }.toList.init
        val p = Geopolygon(geos)
        infolog("found polygon: " + p)
        p
      }
      else {
        null
      }
    }.filter { a =>
      a != null
    }
  }
  
  def listMaps() {
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

  def listFeatures() {
    features.foreach { a =>
      println(a.getKml.getBlob)
    }
  }

  private[this] def features() = {
    val feed = new URL("http://maps.google.com/maps/feeds/features/211158563591768037502/000488ebf9fe962fc40ab/full")
    val result = service.getFeed(feed, classOf[FeatureFeed])
    val buffer = new ListBuffer[FeatureEntry]
    val entries = result.getEntries
    for (i <- 0 to entries.size-1) {
      buffer += entries.get(i)
    }
    buffer.toList
  }
}
