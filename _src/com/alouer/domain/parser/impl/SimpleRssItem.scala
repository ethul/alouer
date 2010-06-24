/**
 * 
 */
package com.alouer.domain.parser.impl

import com.alouer.domain.parser.RssItemizable

/**
 * @author ethul
 *
 */
case class SimpleRssItem(
  link: String,
  title: String, 
  description: String, 
  address: String,
  date: String
) extends RssItemizable
