/**
 * 
 */
package com.alouer.domain.presentation

import com.alouer.domain.parser.RssItemizable
import com.alouer.domain.util.Geolocatable

/**
 * @author ethul
 *
 */
case class MapFeature(item: RssItemizable, geo: Geolocatable)
extends RssItemizable 
with Geolocatable {
  def link() = item link
  def title() = item title
  def description() = item description
  def address() = item address
  def date() = item date
  def latitude() = geo latitude
  def longitude() = geo longitude
  def known() = geo known
}
