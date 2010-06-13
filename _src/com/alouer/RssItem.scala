/**
 * 
 */
package com.alouer

/**
 * @author ethul
 *
 */
case class RssItem(link: String, title: String, description: String, address: String) {
  override def toString(): String = link + " : " + title + " : " + address
}
