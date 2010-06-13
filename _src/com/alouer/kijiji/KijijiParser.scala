/**
 * 
 */
package com.alouer.kijiji

import com.alouer.Geocoder
import com.alouer.MapMarker
import com.alouer.{RssParser,RssItem}
import com.alouer.util.{Logger,TagSoupFactoryAdapter}
import scala.xml.Elem
import java.net.URL

/**
 * @author ethul
 *
 */
class KijijiParser(geocoder: Geocoder) extends RssParser(geocoder) {
  private[this] val infolog = Logger.log(Logger.Info) _
  
  def parse(rss: Elem): Seq[MapMarker] = {
    val pubdate = (rss \\ "pubDate")(0).text
    infolog("kijiji pubdate: " + pubdate)
    val items = rss \\ "item"
    items.map { a =>
      val link = (a \ "link").text
      val title = (a \ "title").text
      val description = (a \ "description").text
      val address = formatAddress(link)
      val geolocation = geocoder.encode(address)
      val item = new RssItem(link, unquote(title), unquote(removeEscapes(description)), address)
      MapMarker(item, geolocation)
    }
  } 
  
  private[this] def formatAddress(link: String) = {
    val url = new URL(link)
    val soup = new TagSoupFactoryAdapter
    infolog("fetching address from link: " + link)
    val html = soup.load(url.openStream)
    val tds = html \\ "td"
    val i = tds.indexWhere(a => a.text == "Adresse" || a.text == "Address")
    val array = tds(i+1).text.split('\n')
    val address = array(0).stripPrefix(" ")
    address
  }
}