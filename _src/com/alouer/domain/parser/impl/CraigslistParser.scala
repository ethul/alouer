/**
 * 
 */
package com.alouer.domain.parser.impl

import com.alouer.domain.parser.AbstractParser
import com.alouer.service.util.Logger
import java.net.{URL,URLDecoder}

/**
 * @author ethul
 *
 */
case class CraigslistParser(feed: String) extends AbstractParser(feed) {
  private[this] val infolog = Logger.log(Logger.Info) _
  private[this] val noaddress = ""
  private[this] val encoding = "utf-8"

  protected[this] def parseAddress(link: String): String = {
    infolog("fetching address from link: " + link)
    val soup = new TagSoupFactoryAdapter
    val html = soup.load(connection(link).getInputStream)
    val as = html \\ "a"
    val i = as.indexWhere(a => a.text == "google map")
    if (i != -1) {
      val href = new URL(as(i).attribute("href").get.text)
      URLDecoder.decode(href.getQuery, encoding).stripPrefix("q=loc: ")
    }
    else {
      noaddress
    }
  }
}