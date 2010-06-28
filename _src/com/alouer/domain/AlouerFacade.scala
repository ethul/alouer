/**
 * 
 */
package com.alouer.domain

import com.alouer.domain.parser.impl.{CraigslistParser,CompositeParser,KijijiParser,StatefulParserDecorator}
import com.alouer.domain.presentation.{GoogleMaps,MapFeature}
import com.alouer.domain.util.{Geocoder,Geolocatable,Geolocation,Geopolygon,Geocache}
import com.alouer.service.persistence.{Cache,FileCache}
import com.alouer.ui._
import com.alouer.service.util.{Logger,TimeAccessor,Statistics}
import scala.actors.Actor._
import java.io.FileWriter
import java.util.concurrent.{Executors,TimeUnit}

/**
 * @author ethul
 *
 */
case class AlouerFacade() {
  private[this] val info = Logger.log(Logger.Info) _
  private[this] val warn = Logger.log(Logger.Warning) _
  private[this] val kijiji = "http://montreal.kijiji.ca/f-SearchAdRss?AdType=2&AddressLatitude=45.51228&AddressLongitude=-73.55439&CatId=37&Location=80002&MapAddress=Montr%C3%A9al&distance=15&maxPrice=1,000&minPrice=500&useLocalAddress=false"
  private[this] val craigs = "http://montreal.en.craigslist.ca/search/apa?query=&minAsk=500&maxAsk=1000&bedrooms=&format=rss"
  private[this] val cachefile = "/home/ethul/tmp/alouer.cache"
  private[this] val geocachefile = "/home/ethul/tmp/alouer.geocache"
  private[this] val datecacheSeparator = " "
  private[this] val geocacheSeparator = "="
  private[this] val username = "montreal.alouermap"
  private[this] val password = "bF7nwO@0F"

  def createMapItems() {
    val state = FileCache[String,String](cachefile, datecacheSeparator)
    val geocoder = Geocoder(Geocache[String,Geolocatable](FileCache[String,String](geocachefile, geocacheSeparator)))
    val craigsParser = StatefulParserDecorator(CraigslistParser(craigs), state)
    val kijijiParser = StatefulParserDecorator(KijijiParser(kijiji), state)
    val compositeParser = CompositeParser(craigsParser :: kijijiParser :: Nil)
    val polygons = GoogleMaps(username, password).getFeaturePolygons
    
    polygons foreach { _ subscribe Statistics }
    geocoder subscribe Statistics
    craigsParser subscribe Statistics
    kijijiParser subscribe Statistics
    
    val rssitems = compositeParser.parse
    val geolocations = geocoder encode rssitems.map(_.address)
    val markers = rssitems.zip(geolocations).map { a => MapFeature(a._1, a._2) }
    val markersUnknown = markers filter { a => !a.known }
    val markersInPolygon = polygons flatMap { _ retain markers }
    val markersToMap = markersInPolygon ::: markersUnknown
    GoogleMaps(username, password) createFeatures markersToMap.asInstanceOf[List[MapFeature]]
  }
  
  def deleteMapItems() {
    GoogleMaps(username, password) deleteFeatures
  }
  
  def showMapItems() {
    GoogleMaps(username, password) listFeatures
  }
}