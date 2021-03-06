/**
 * 
 */
package com.alouer.domain

import com.alouer.domain.parser.RssParsable
import com.alouer.domain.parser.impl.{CraigslistParser,CompositeParser,KijijiParser,StatefulParserDecorator}
import com.alouer.domain.presentation.{GoogleMaps,MapFeature}
import com.alouer.domain.service.geocoder.AbstractGeocoder
import com.alouer.domain.service.geocoder.impl.{DailyBoundedGeocoder,GoogleGeocoder,ThrottledGeocoder}
import com.alouer.domain.util.{Geolocatable,Geolocation,Geopolygon,Geocache}
import com.alouer.service.persistence.{Cache,Configuration,FileCache}
import com.alouer.service.util.{Logger,TimeAccessor,Statistics}

/**
 * @author ethul
 *
 */
case class AlouerFacade() {
  private[this] val info = Logger.log(Logger.Info)
  private[this] val warn = Logger.log(Logger.Warning)
  private[this] val rsscacheSeparator = " "
  private[this] val geocacheSeparator = "="
    
  // initialization variables
  private[this] var geocoder: AbstractGeocoder = _
  private[this] var parser: RssParsable = _
  private[this] var maps: GoogleMaps = _
    
  def initialize() {
    val geocachefile = Configuration.get("google.maps.geocache").get
    val geocache = Geocache[String,Geolocatable](FileCache[String,String](geocachefile, geocacheSeparator))
    geocoder = new GoogleGeocoder(geocache) with ThrottledGeocoder with DailyBoundedGeocoder
    geocoder subscribe Statistics
    
    val kijiji = Configuration.get("rss.feed.kijiji").get
    val craigs = Configuration.get("rss.feed.craigslist").get
    val rsscache = Configuration.get("rss.feed.cache").get
    val geostate = FileCache[String,String](rsscache, rsscacheSeparator)
    val craigsParser = StatefulParserDecorator(CraigslistParser(craigs), geostate)
    val kijijiParser = StatefulParserDecorator(KijijiParser(kijiji), geostate)
    craigsParser subscribe Statistics
    kijijiParser subscribe Statistics
    parser = CompositeParser(craigsParser :: kijijiParser :: Nil)
    
    val username = Configuration.get("google.maps.username").get
    val password = Configuration.get("google.maps.password").get
    maps = GoogleMaps(username, password)
  }

  def createMapItems() {
    val polygons = maps.getFeaturePolygons
    polygons foreach { _ subscribe Statistics }
    val rssitems = parser.parse
    //val geolocations = geocoder encode rssitems.map(_.address)
    //val markers = rssitems.zip(geolocations).map { a => MapFeature(a._1, a._2) }
    //val markersUnknown = markers filter { a => !a.known }
    //val markersInPolygon = polygons flatMap { _ retain markers }
    //val markersToMap = markersInPolygon ::: markersUnknown
    //maps createFeatures markersToMap.asInstanceOf[List[MapFeature]]
    maps createFeatures rssitems
  }
  
  def deleteMapItems() {
    maps deleteFeatures
  }
  
  def showMapItems() {
    maps listFeatures
  }
}