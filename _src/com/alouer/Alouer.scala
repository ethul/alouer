/**
 * 
 */
package com.alouer

import com.alouer.craigslist.CraigslistParser
import com.alouer.kijiji.KijijiParser
import com.alouer.util.Logger
import java.io.FileWriter

/**
 * @author ethul
 *
 */
object Alouer {
  private[this] val kijiji = "http://montreal.kijiji.ca/f-SearchAdRss?AdType=2&AddressLatitude=45.51228&AddressLongitude=-73.55439&CatId=214&Location=2&MapAddress=Montr%C3%A9al&distance=15&maxPrice=1,000&minPrice=650&useLocalAddress=false"
  private[this] val craigslist = "http://montreal.en.craigslist.ca/search/apa?query=&minAsk=600&maxAsk=1000&bedrooms=2&format=rss"
  private[this] val mapfile = "/tmp/alouer.html"
    
  def main(args: Array[String]) {
    val infolog = Logger.log(Logger.Info) _
    val geocoder = new Geocoder(new FileGeocache("/tmp/alouer.geocache"))
    
    val craigsParser = new CraigslistParser(geocoder)
    val craigsMarkers = craigsParser.parse(craigslist)
    val kijijiParser = new KijijiParser(geocoder)
    val kijijiMarkers = kijijiParser.parse(kijiji)
    val map = new MapGenerator
    val html = map.generate(craigsMarkers.filter(a => a.geolocation.latitude != "0") ++ kijijiMarkers)
    val writer = new FileWriter(mapfile)
    writer.write(html)
    writer.close
    infolog("map written to: " + mapfile)
    Logger.close
  }
}