/**
 * 
 */
package com.alouer

import com.alouer.util.Logger
import scala.xml.{Elem,XML}
import java.net.URL

/**
 * @author ethul
 *
 */
abstract class RssParser(geocoder: Geocoder) {
  private[this] val infolog = Logger.log(Logger.Info) _
  
  def parse(rss: Elem): Seq[MapMarker]
  
  def parse(location: String): Seq[MapMarker] = {
    val url = new URL(location)
    infolog("loading rss: " + location)
    val rss = XML.load(url.openStream)
    parse(rss)
  }
  
  protected final def removeEscapes(value: String) = {
    value.filter(a => a >= ' ').toString
  }
  
  protected final def unquote(value: String) = {
    value.replace("\"", "").replace("'", "").replace("`","")
  }
}