/**
 * 
 */
package com.alouer.craigslist

import com.alouer.Geocoder
import com.alouer.Geopolygon
import com.alouer.RssParser
import com.alouer.util.{Cache,Logger,TagSoupFactoryAdapter,Statistics}
import java.net.{URL,URLDecoder}

/**
 * @author ethul
 *
 */
class CraigslistParser(geocoder: Geocoder, cache: Cache, polygon: Geopolygon)
extends RssParser(geocoder, cache, polygon) {
  private[this] val infolog = Logger.log(Logger.Info) _
  private[this] val encoding = "utf-8"

  def dateKey(): String = "craigslist"
  
  def parseAddress(link: String): String = {
    val url = new URL(link)
    val soup = new TagSoupFactoryAdapter
    infolog("fetching address from link: " + link)
    val html = soup.load(url.openStream)
    val as = html \\ "a"
    val i = as.indexWhere(a => a.text == "google map")
    if (i != -1) {
      val href = new URL(as(i).attribute("href").get.text)
      val address = URLDecoder.decode(href.getQuery, encoding).stripPrefix("q=loc: ")
      address
    }
    else {
      ""
    }
  }
  
  def tallyItems(tally: Int) {
    if (tally > 0) {
      Range(1, tally).foreach { a =>
        Statistics incCraigsRssItems
      }
    }
  }
  
  def tallyNewItems(tally: Int) {
    if (tally > 0) {
      Range(1, tally).foreach { a =>
        Statistics incCraigsNewRssItems
      }
    }
  }
}