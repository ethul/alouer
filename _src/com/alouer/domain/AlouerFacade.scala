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
  
  // only map points which lie inside the polygon
  private[this] val plateau = 
    Geolocation("45.543058172101794","-73.59663963317871") :: 
    Geolocation("45.52324728409929" ,"-73.55376720428467") :: 
    Geolocation("45.50724918499527" ,"-73.5690021514892")  :: 
    Geolocation("45.52697551076455" ,"-73.6129474639892")  :: Nil
    
  private[this] val ndg =
    Geolocation("45.49855658189799" ,"-73.62762451171875") :: 
    Geolocation("45.474757852656204","-73.60376358032227") :: 
    Geolocation("45.466752481053454","-73.61895561218262") :: 
    Geolocation("45.476834248283836","-73.64414691925049") :: Nil

  def createMapItems() {
    val state = FileCache[String,String](cachefile, datecacheSeparator)
    val geocoder = Geocoder(Geocache[String,Geolocatable](FileCache[String,String](geocachefile, geocacheSeparator)))
    val plateauPolygon = Geopolygon(plateau)
    val ndgPolygon = Geopolygon(ndg)
    val craigsParser = StatefulParserDecorator(CraigslistParser(craigs), state)
    val kijijiParser = StatefulParserDecorator(KijijiParser(kijiji), state)
    val compositeParser = CompositeParser(craigsParser :: kijijiParser :: Nil)
    
    geocoder subscribe Statistics
    plateauPolygon subscribe Statistics
    ndgPolygon subscribe Statistics
    craigsParser subscribe Statistics
    kijijiParser subscribe Statistics
    
    val rssitems = compositeParser.parse
    val geolocations = geocoder encode rssitems.map(_.address)
    val markers = rssitems.zip(geolocations).map { a => MapFeature(a._1, a._2) }
    val markersUnknown = markers filter { a => !a.known }
    val markersInPlateau = plateauPolygon retain markers
    val markersInNdg = ndgPolygon retain markers
    val markersToMap = markersInPlateau ::: markersInNdg ::: markersUnknown
    GoogleMaps(username, password) createFeatures markersToMap.asInstanceOf[List[MapFeature]]
  }
  
  def deleteMapItems() {
    GoogleMaps(username, password) deleteFeatures
  }
}