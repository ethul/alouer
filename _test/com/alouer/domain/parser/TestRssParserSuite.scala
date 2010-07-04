/**
 * 
 */
package com.alouer.domain.parser

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
  
  test("website parsing for craigslist") {
    val html = """
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> 
<html> 
<head> 
  <title>AWESOME &amp; NEAT APARTMENT AVAILABLE</title> 
  <meta name="robots" content="NOARCHIVE,NOFOLLOW"> 
  <link type="text/css" rel="stylesheet" media="all" href="http://www.craigslist.org/styles/craigslist.css?v=5"> 
</head> 
 
<body class="posting"> 
 
 
<div class="bchead"> 
  <a id="ef" href="/email.friend?postingID=1824192751">email this posting to a friend</a> 
  <a href="http://montreal.en.craigslist.ca/">montreal craigslist</a> &gt;
  
   <a href="http://montreal.en.craigslist.ca/hhh/">housing</a> &gt;
    <a href="http://montreal.en.craigslist.ca/apa/">apts/housing for rent</a> 
</div> 
 
  
 
  <div id="flags"> 
    <div id="flagMsg"> 
      please <a href="http://www.craigslist.org/about/help/flags_and_community_moderation">flag</a> with care:
    </div> 
    <div id="flagChooser"> 
      <br> 
      <a class="fl" id="flag16" href="/flag/?flagCode=16&amp;postingID=1824192751"
        title="Wrong category, wrong site, discusses another post, or otherwise misplaced"> 
        miscategorized</a> 
      <br> 
 
      <a class="fl" id="flag28" href="/flag/?flagCode=28&amp;postingID=1824192751"
        title="Violates craigslist Terms Of Use or other posted guidelines"> 
        prohibited</a> 
      <br> 
 
      <a class="fl" id="flag15" href="/flag/?flagCode=15&amp;postingID=1824192751"
        title="Posted too frequently, in multiple cities/categories, or is too commercial"> 
        spam/overpost</a> 
      <br> 
 
      <a class="fl" id="flag9" href="/flag/?flagCode=9&amp;postingID=1824192751"
        title="Should be considered for inclusion in the Best-Of-Craigslist"> 
        best of craigslist</a> 
      <br> 
    </div> 
  </div> 
 
 
<h2>$800 / 2br - AWESOME &amp; NEAT APARTMENT AVAILABLE (Rue Baile)</h2> 
<hr> 
Date: 2010-07-03,  1:42PM EDT<br> 
Reply to: <a href="mailto:hous-bztn6-1824192751@craigslist.org?subject=%24800%20%2F%202br%20-%20AWESOME%20%26amp%3B%20NEAT%20APARTMENT%20AVAILABLE%20(Rue%20Baile)&amp;body=%0A%0Ahttp%3A%2F%2Fmontreal.en.craigslist.ca%2Fapa%2F1824192751.html%0A">hous-bztn6-1824192751@craigslist.org</a> <sup>[<a href="http://www.craigslist.org/about/help/replying_to_posts" target="_blank">Errors when replying to ads?</a>]</sup><br> 
<hr> 
<br> 
<div id="userbody"> 
SPACIOUS $ LOVELY HOME
<br> 
LARGE STORAGE
<br> 
6 APPLIANCES IN LARGE KITCHEN
<br> 
WASHER&DRYER
<br> 
CLOSE TO SCHOOLS, SHOPPING & TRANSIT. 
<br> 
UTILITIES INCLUDED AND PETS OK.
<br> 
 
<br> 
AVAILABLE FURNISHED AND UNFURNISHED
<br> 
<!-- START CLTAGS --> 
 
 
<br><br><ul class="blurbs"> 
<li><!-- CLTAG catsAreOK=on -->cats are OK - purrr
<li><!-- CLTAG dogsAreOK=on -->dogs are OK - wooof
<li> <!-- CLTAG GeographicArea=Rue Baile -->Location: Rue Baile
<li>it's NOT ok to contact this poster with services or other commercial interests</ul> 
<!-- END CLTAGS --> 
    <table summary="craigslist hosted images"> 
      <tr> 
        <td align="center"><img src="http://images.craigslist.org/3kc3m43oa5Y55W25P0a7307186d9299641f04.jpg" alt="image 1824192751-0"></td> 
        <td align="center"><img src="http://images.craigslist.org/3nd3kd3pd5O05V05W1a73f110391088b21323.jpg" alt="image 1824192751-1"></td> 
      </tr> 
      <tr> 
        <td align="center"><img src="http://images.craigslist.org/3ka3pb3l05Q25X25R4a7350fe0d31b1ed12ac.jpg" alt="image 1824192751-2"></td> 
        <td align="center"></td> 
      </tr> 
    </table> 
 
</div> 
PostingID: 1824192751<br> 
 
 
<br> 
 
<hr> 
<ul class="clfooter"> 
  <li>Copyright &copy; 2010 craigslist, inc.</li> 
  <li><a href="http://www.craigslist.org/about/terms.of.use.html">terms of use</a></li> 
  <li><a href="http://www.craigslist.org/about/privacy_policy">privacy policy</a></li> 
  <li><a href="/forums/?forumID=8">feedback forum</a></li> 
</ul> 
 
<script type="text/javascript" src="http://www.craigslist.org/js/jquery-1.4.2.js"></script> 
<script type="text/javascript" src="http://www.craigslist.org/js/postings.js"></script> 
<script type="text/javascript"><!--
  pID = 1824192751;
-->
</script> 
</body> 
</html>     
    """
      
    import java.net.{URL,URLDecoder}
    val encoding = "utf-8"
    val parser = new AbstractParser(null) {
      def feed = ""
      def testParse(s: String) = parseAddress(s)
      def parseAddress(link: String): String = {
        val soup = new TagSoupFactoryAdapter
        val html = soup.loadString(link)
        val as = html \\ "a"
        val i = as.indexWhere(a => a.text == "google map")
        if (i != -1) {
          val href = new URL(as(i).attribute("href").get.text)
          URLDecoder.decode(href.getQuery, encoding).stripPrefix("q=loc: ")
        }
        else {
          "no address"
        }
      }
    }
    
    println(parser.testParse(html))
  }

  private case class MockRssParser(feed: String) extends AbstractParser(feed) {
    def parseAddress(item: String): String = ""
    def testRemoveEscapes(value: String) = removeNonPrintables(value)
  }
}
