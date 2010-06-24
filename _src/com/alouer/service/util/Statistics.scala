/**
 * 
 */
package com.alouer.service.util

import com.alouer.service.monitor.{Notifiable,Subscribable}
import scala.collection.mutable.HashMap

/**
 * @author ethul
 *
 */
object Statistics extends Subscribable {
  var geolookups = 0
  var geopolygon = 0
  val newRssItems = HashMap[String,Int]()
  
  def notify(notification: Notifiable) {
    notification match {
      case GeolookupAccumulator(a) => geolookups += a
      case GeopolygonAccumulator(a) => geopolygon += a
      case NewRssItemAccumulator(a,b) => newRssItems get a match {
        case Some(x) => newRssItems += (a -> (b + x))
        case None => newRssItems += (a -> b)
      }
    }
  }

  def reset() {
    geopolygon = 0
    newRssItems clear
  }
  
  override def toString() = {
    "geolookups = " + geolookups + "\n" +
    "geopolygon = " + geopolygon + "\n" +
    "new rss items = " + newRssItems
  }
  
  case class GeolookupAccumulator(n: Int) extends Notifiable
  case class GeopolygonAccumulator(n: Int) extends Notifiable
  case class NewRssItemAccumulator(feed: String, n: Int) extends Notifiable
}

