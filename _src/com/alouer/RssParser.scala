/**
 * 
 */
package com.alouer

import com.alouer.util.{Cache,Logger,Statistics}
import scala.xml.{Elem,Node,XML}
import java.net.URL

/**
 * @author ethul
 *
 */
abstract class RssParser(geocoder: Geocoder, cache: Cache, polygon: Geopolygon) {
  private[this] val infolog = Logger.log(Logger.Info) _
  private[this] val unknownLat = "0"
  private[this] val unknownLng = "0"
  
  def dateKey(): String
  def parseAddress(link: String): String
  def tallyItems(tally: Int): Unit
  def tallyNewItems(tally: Int): Unit
  
  def parse(location: String): Seq[MapMarker] = {
    val url = new URL(location)
    infolog("loading rss: " + location)
    val rss = XML.load(url.openStream)
    val items = rss \\ "item"
    
    val newitems = items.filter { a =>
      val itemDate = parseDate(a)
      val cachedDate = 
        if (cache.contains(dateKey)) {
          cache.get(dateKey).get
        }
        else {
          "0"
        }
      
      val formattedDate = formatDate(itemDate)
      infolog("comparing dc dates: " + formattedDate + " to " + cachedDate)
      formattedDate.toLong > cachedDate.toLong
    }
    
    tallyItems(items length)
    tallyNewItems(newitems length)
    
    if (!newitems.isEmpty) {
      val formattedDate = formatDate(parseDate(newitems(0)))
      cache.put(dateKey, formattedDate)
    
      newitems.map { a =>
        val link = (a \ "link").text
        val title = (a \ "title").text
        val description = (a \ "description").text
        val address = parseAddress(link)
        val geolocation = geocoder.encode(address)
        val item = new RssItem(link, removeQuotes(title), removeQuotes(removeNonPrintables(description)), address)
        MapMarker(item, geolocation)
      }.filter { a =>
        if (a.geolocation.latitude == unknownLat && a.geolocation.longitude == unknownLng) {
          infolog("keeping unknown lat and long")
          true
        }
        else if (polygon.contains(a.geolocation)) {
          infolog("keeping geolocation in the polygon")
          Statistics.incGeopolygon
          true
        }
        else {
          infolog("discarding geolocation not in the polygon: " + a.geolocation)
          false
        }
      }
    }
    else {
      infolog("no updates")
      Nil
    }
  }
  
  protected def parseDate(node: Node): String = {
    val i = node.child.indexWhere(_.label == "date")
    node.child(i).text
  }
  
  protected final def formatDate(date: String) = {
    // 2010-06-13T20:42:27Z
    date.take(19).replace("-","").replace(":","").replace("T","")
  }
  
  protected final def removeNonPrintables(value: String) = {
    value.filter(a => a >= ' ').toString
  }
  
  protected final def removeQuotes(value: String) = {
    value.replace("\"", "").replace("'", "").replace("`","")
  }
}