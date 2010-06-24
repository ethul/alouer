/**
 * 
 */
package com.alouer.domain.parser

/**
 * @author ethul
 *
 */
trait RssParsable {
  def feed(): String
  def parse(): List[RssItemizable]
}