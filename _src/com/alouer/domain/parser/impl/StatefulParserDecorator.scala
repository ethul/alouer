/**
 * 
 */
package com.alouer.domain.parser.impl

import com.alouer.domain.parser.{DecoratableParser,RssItemizable,RssParsable}
import com.alouer.service.monitor.{Notifiable,Observable,Subscribable}
import com.alouer.service.persistence.Cache
import com.alouer.service.util.{Logger,Md5,Statistics}

/**
 * @author ethul
 *
 */
case class StatefulParserDecorator(parser: RssParsable, state: Cache[String,String])
extends RssParsable with DecoratableParser with Observable {
  private[this] val infolog = Logger.log(Logger.Info)
  private[this] val zero = "0"
    
  def feed(): String = parser feed
  
  def parse(): List[RssItemizable] = {
    var max = "0"
    val key = Md5 sum feed
    val filtered = parser.parse filter { a =>
      val current = 
        if (state.contains(key)) {
          state.get(key) match {
            case Some(x) => x
            case None => zero
          }
        }
        else {
          zero
        }
      
      val date = formatDate(a date)
      infolog("comparing dates: " + date + " to " + current)
      val isnew = date.toLong > current.toLong
      // keep the max new item date
      if (isnew && date.toLong  > max.toLong) {
        max = date
      }
      isnew
    }
    notify(Statistics.NewRssItemAccumulator(key, filtered.length))
    if (max != "0") {
      state.put(key,max)
    }
    filtered
  }
  
  private[this] def formatDate(date: String) = date match {
    case "" => "0"
    case null => "0"
    case x => {
      // 2010-06-13T20:42:27Z
      x.take(19).replace("-","").replace(":","").replace("T","")
    }
  }
}