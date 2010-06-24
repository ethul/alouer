/**
 * 
 */
package com.alouer.domain.parser

/**
 * @author ethul
 *
 */
trait RssItemizable {
  def link(): String
  def title(): String
  def description(): String
  def address(): String
  def date(): String
}