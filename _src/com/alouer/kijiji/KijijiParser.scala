/**
 * 
 */
package com.alouer.kijiji

import com.alouer.Geocoder
import com.alouer.Geopolygon
import com.alouer.{RssParser,RssItem}
import com.alouer.util.{Cache,Logger,TagSoupFactoryAdapter,Statistics}
import java.net.URL

/**
 * @author ethul
 *
 */
class KijijiParser(geocoder: Geocoder, cache: Cache, polygon: Geopolygon)
extends RssParser(geocoder, cache, polygon) {
  private[this] val infolog = Logger.log(Logger.Info) _
  
  def dateKey(): String = "kijiji"
  
  def parseAddress(link: String): String = {
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
  
  def tallyItems(tally: Int) {
    if (tally > 0) {
      Range(1, tally).foreach { a =>
        Statistics incKijijiRssItems
      }
    }
  }
  
  def tallyNewItems(tally: Int) {
    if (tally > 0) {
      Range(1, tally).foreach { a =>
        Statistics incKijijiNewRssItems
      }
    }
  }
}