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
  private[this] val points = 
    Geolocation("45.543058172101794","-73.59663963317871") :: 
    Geolocation("45.52324728409929" ,"-73.55376720428467") :: 
    Geolocation("45.50724918499527" ,"-73.5690021514892")  :: 
    Geolocation("45.52697551076455" ,"-73.6129474639892")  :: Nil

  def createMapItems() {
    val state = FileCache[String,String](cachefile, datecacheSeparator)
    val geocoder = Geocoder(Geocache[String,Geolocatable](FileCache[String,String](geocachefile, geocacheSeparator)))
    val geopolygon = Geopolygon(points)
    val craigsParser = StatefulParserDecorator(CraigslistParser(craigs), state)
    val kijijiParser = StatefulParserDecorator(KijijiParser(kijiji), state)
    val compositeParser = CompositeParser(craigsParser :: kijijiParser :: Nil)
    
    geocoder subscribe Statistics
    geopolygon subscribe Statistics
    craigsParser subscribe Statistics
    kijijiParser subscribe Statistics
    
    val rssitems = compositeParser.parse
    val geolocations = geocoder encode rssitems.map(_.address)
    val markers = rssitems.zip(geolocations).map { a => MapFeature(a._1, a._2) }
    val markersInPolygon = geopolygon retain markers
    //GoogleMaps(username, password) createFeatures markersInPolygon.asInstanceOf[List[MapFeature]]
  }
  
  def deleteMapItems() {
    GoogleMaps(username, password) deleteFeatures
  }
}