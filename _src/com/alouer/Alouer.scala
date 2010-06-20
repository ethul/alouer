/**
 * 
 */
package com.alouer

import com.alouer.craigslist.CraigslistParser
import com.alouer.google.MapsFacade
import com.alouer.kijiji.KijijiParser
import com.alouer.util.{Cache,FileCache,Logger,TimeAccessor,Statistics}
import scala.actors.Actor._
import java.io.FileWriter
import java.util.concurrent.{Executors,TimeUnit}

/**
 * @author ethul
 *
 */
object Alouer {
  private[this] val kijiji = "http://montreal.kijiji.ca/f-SearchAdRss?AdType=2&AddressLatitude=45.51228&AddressLongitude=-73.55439&CatId=37&Location=80002&MapAddress=Montr%C3%A9al&distance=15&maxPrice=1,000&minPrice=500&useLocalAddress=false"
  private[this] val craigs = "http://montreal.en.craigslist.ca/search/apa?query=&minAsk=500&maxAsk=1000&bedrooms=&format=rss"
  private[this] val mapfile = "/home/ethul/tmp/alouer.html"
  private[this] val cachefile = "/home/ethul/tmp/alouer.cache"
  private[this] val geocachefile = "/home/ethul/tmp/alouer.geocache"
  private[this] val maxGeoLookups = 2000
  
  // only map points which lie inside the polygon
  private[this] val points = 
    Geolocation("45.543058172101794","-73.59663963317871") :: 
    Geolocation("45.52324728409929" ,"-73.55376720428467") :: 
    Geolocation("45.50724918499527" ,"-73.5690021514892")  :: 
    Geolocation("45.52697551076455" ,"-73.6129474639892")  :: Nil
    
  def main(args: Array[String]) {
    val infolog = Logger.log(Logger.Info) _
    val warnlog = Logger.log(Logger.Warning) _
    val cache = new FileCache(cachefile)
    val geocoder = new Geocoder(new FileGeocache(geocachefile))
    val geopolygon = Geopolygon(points)
    val craigsParser = new CraigslistParser(geocoder, cache, geopolygon)
    val kijijiParser = new KijijiParser(geocoder, cache, geopolygon)
    val maps = new MapsFacade
    val service = maps.service
    val scheduler = Executors.newSingleThreadScheduledExecutor
    
    val task = () => {
      maps markup(service, craigsParser.parse(craigs) ++ kijijiParser.parse(kijiji))
    }
    
    val delete = () => maps.deleteFeatures(service)
    
    val cli = actor {
      loop {
        react {
          case Input("stop") => {
            scheduler.shutdown
            if (!scheduler.awaitTermination(120L, TimeUnit.SECONDS)) {
              println("cannot schedule, previous task not shutdown")
              println("forcing shutdown")
              scheduler.shutdownNow
            }
            Logger.close
            exit
          }
          case Input("delete") => {
            delete()
          }
          case Input("schedule") => {
            println("scheduling the task every half hour")
            scheduler.scheduleWithFixedDelay(new Runnable {
              def run() {
                if (Statistics.getOverallGeoLookups > maxGeoLookups) {
                  println(Statistics.getOverallGeoLookups + " is over the max number of geo lookups")
                  warnlog(Statistics.getOverallGeoLookups + " is over the max number of geo lookups")
                  println("forcing shutdown")
                  scheduler.shutdownNow
                }
                else {
                  println("running task at " + TimeAccessor.nowString)
                  task()
                  infolog("\n" + Statistics)
                  println("\n" + Statistics)
                  Statistics reset
                }
              }
            }, 5L, 60L * 30L, TimeUnit.SECONDS)
          }
          case Input(input) => {
            println("unknown: " + input)
          }
          case Ready => {
            print("alouer$")
            self ! Input(Console.readLine) 
            self ! Ready
          }
        }
      }
    }
    
    cli ! Ready
  }
}