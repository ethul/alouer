/**
 * 
 */
package com.alouer.domain.parser.impl

import com.alouer.domain.parser.{RssItemizable,RssParsable}

/**
 * @author ethul
 *
 */
case class CompositeParser(parsers: List[RssParsable]) extends RssParsable {
  def feed(): String = parsers.foldLeft("") { _ + _ }
  def parse(): List[RssItemizable] = parsers.flatMap(a => a.parse)
}