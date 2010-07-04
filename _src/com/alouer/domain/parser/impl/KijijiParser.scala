/**
 * 
 */
package com.alouer.domain.parser.impl

import com.alouer.domain.parser.AbstractParser
import com.alouer.service.util.Logger

/**
 * @author ethul
 *
 */
case class KijijiParser(feed: String) extends AbstractParser(feed) {
  private[this] val infolog = Logger.log(Logger.Info) _
  private[this] val error = Logger.log(Logger.Error) _
  private[this] val noaddress = ""
  
  protected[this] def parseAddress(link: String): String = {
    infolog("fetching address from link: " + link)
    val soup = new TagSoupFactoryAdapter
    val tds = try {
      val html = soup.load(connection(link))
      html \\ "td"
    }
    catch {
      case e: Exception => {
        error(e)
        Nil
      }
    }
    val i = tds.indexWhere(a => a.text == "Adresse" || a.text == "Address")
    if (i != -1) {
      val array = tds(i+1).text.split('\n')
      array(0).stripPrefix(" ")
    }
    else {
      noaddress
    }
  }
}