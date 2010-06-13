/**
 * 
 */
package com.alouer.craigslist

import com.alouer.Geocoder
import com.alouer.MapMarker
import com.alouer.{RssParser,RssItem}
import com.alouer.util.{Logger,TagSoupFactoryAdapter}
import scala.xml.Elem
import java.net.{URL,URLDecoder}

/**
 * @author ethul
 *
 */
class CraigslistParser(geocoder: Geocoder) extends RssParser(geocoder) {
  private[this] val infolog = Logger.log(Logger.Info) _
  private[this] val encoding = "utf-8"
  
  def parse(rss: Elem): Seq[MapMarker] = {
    val items = rss \\ "item"
    items.map { a =>
      val link = (a \ "link").text
      val title = (a \ "title").text
      val description = (a \ "description").text
      val address = parseAddress(link)
      val geolocation = geocoder.encode(address)
      val item = new RssItem(link, unquote(title), unquote(removeEscapes(unquote(description))), address)
      MapMarker(item, geolocation)
    }
  }
  
  private[this] def parseAddress(link: String) = {
    val url = new URL(link)
    val soup = new TagSoupFactoryAdapter
    infolog("fetching address from link: " + link)
    val html = soup.load(url.openStream)
    val as = html \\ "a"
    val i = as.indexWhere(a => a.text == "google map")
    val address = if (i != -1) {
      val href = new URL(as(i).attribute("href").get.text)
      val address = URLDecoder.decode(href.getQuery, encoding).stripPrefix("q=loc: ")
      address
    }
    else {
      ""
    }
    address
  }
}