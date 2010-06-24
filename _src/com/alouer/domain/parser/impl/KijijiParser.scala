/**
 * 
 */
package com.alouer.domain.parser.impl

import com.alouer.domain.parser.AbstractParser
import com.alouer.service.util.Logger
import java.net.URL


/**
 * @author ethul
 *
 */
case class KijijiParser(feed: String) extends AbstractParser(feed) {
  private[this] val infolog = Logger.log(Logger.Info) _
  
  protected[this] def parseAddress(link: String): String = {
    val url = new URL(link)
    val soup = new TagSoupFactoryAdapter
    infolog("fetching address from link: " + link)
    val html = soup.load(url.openStream)
    val tds = html \\ "td"
    val i = tds.indexWhere(a => a.text == "Adresse" || a.text == "Address")
    val array = tds(i+1).text.split('\n')
    array(0).stripPrefix(" ")
  }
}