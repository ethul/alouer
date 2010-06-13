/**
 * 
 */
package com.alouer

import org.scalatest.FunSuite
import scala.xml.Elem

/**
 * @author ethul
 *
 */
class TestRssParserSuite extends FunSuite {
  private[this] val parser = new MockRssParser(null)
  
  test("strip non ascii characters") {
    val nonascii = """
<p>$100 per night, $500 per week, 
Ideal apartment for tempary stay, near all the service 
Primary Area in Montreal Downtown 
Call 514-998-9986 to make the reservation today. 

Photos? 
www.stanfor</p>

"""
    val expected = "<p>$100 per night, $500 per week, Ideal apartment for tempary stay, near all the service Primary Area in Montreal Downtown Call 514-998-9986 to make the reservation today. Photos? www.stanfor</p>"
    val result = parser.testRemoveEscapes(nonascii)
    assert(expected == result)
  }

  private class MockRssParser(geocoder: Geocoder) extends RssParser(geocoder) {
    def parse(rss: Elem): Seq[MapMarker] = {
      null
    }
    
    def testRemoveEscapes(value: String) = removeEscapes(value)
  }
}
