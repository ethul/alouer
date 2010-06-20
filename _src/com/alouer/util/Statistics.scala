/**
 * 
 */
package com.alouer.util

import scala.collection.mutable.ListBuffer

/**
 * @author ethul
 *
 */
object Statistics {
  private[this] var geoLookups = 0
  private[this] var geocacheHits = 0
  private[this] var geoAttempts = 0
  private[this] var geopolygon = 0
  private[this] var kijijiRssItems = 0
  private[this] var kijijiNewRssItems = 0
  private[this] var craigsRssItems = 0
  private[this] var craigsNewRssItems = 0
  
  private[this] var overallGeoLookups = 0
  private[this] val kijijiNewRssItemsList = ListBuffer[Int]()
  private[this] val craigsNewRssItemsList = ListBuffer[Int]()
  private[this] val geopolygonList = ListBuffer[Int]()

  def incGeoLookups() {
    geoLookups += 1
  }
  
  def incGeocacheHits() {
    geocacheHits += 1
  }
  
  def incGeoAttempts() {
    geoAttempts += 1
  }
  
  def incGeopolygon() {
    geopolygon += 1
  }
  
  def incKijijiRssItems() {
    kijijiRssItems += 1
  }
  
  def incKijijiNewRssItems() {
    kijijiNewRssItems += 1
  }
  
  def incCraigsRssItems() {
    craigsRssItems += 1
  }
  
  def incCraigsNewRssItems() {
    craigsNewRssItems += 1
  }
  
  def getOverallGeoLookups() = overallGeoLookups
  
  def reset() {
    overallGeoLookups += geoLookups
    geopolygonList append geopolygon
    kijijiNewRssItemsList append kijijiNewRssItems
    craigsNewRssItemsList append craigsNewRssItems
    geoLookups = 0
    geocacheHits = 0
    geoAttempts = 0
    geopolygon = 0
    kijijiRssItems = 0
    kijijiNewRssItems = 0
    craigsRssItems = 0
    craigsNewRssItems = 0
  }
  
  override def toString(): String = {
    "geo lookups: " + geoLookups + "\n" +
    "geocache hits: " + geocacheHits + "\n" +
    "geo attemps: " + geoAttempts + "\n" +
    "geo polygon: " + geopolygon + "\n" +
    "kijiji rss items: " + kijijiRssItems + "\n" + 
    "kijiji new rss items: " + kijijiNewRssItems + "\n" + 
    "craigs rss items: " + craigsRssItems + "\n" + 
    "craigs new rss items: " + craigsNewRssItems + "\n" +
    "\n" +
    "overall geo lookups: " + (overallGeoLookups+geoLookups) + "\n" +
    "all items in polygon instances: " + (geopolygonList.toList ::: (geopolygon :: Nil)) + "\n" +
    "all kijiji new item instances: " + (kijijiNewRssItemsList.toList ::: (kijijiNewRssItems :: Nil)) + "\n" +
    "all craigs new item instances: " + (craigsNewRssItemsList.toList ::: (craigsNewRssItems :: Nil)) + "\n"
  }
}