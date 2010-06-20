/**
 * 
 */
package com.alouer

import org.scalatest.FunSuite

/**
 * @author ethul
 *
 */
class TestGeopolygon extends FunSuite {
  test("inside: ray from point intersects top edge") {
    val geos = Geolocation("5","2") :: Geolocation("7","7")  ::  Geolocation("0","8") :: Geolocation("1","1") :: Nil
    val point = Geolocation("4","4")
    assert(Geopolygon(geos).contains(point) == true)
  }
  
  test("inside: ray from point intersects left edge") {
    val geos = Geolocation("8.67","4.5") :: Geolocation("7.78","9.68") :: Geolocation("0.23874","9.00002") :: Geolocation("1.23545","1.234") :: Nil
    val point = Geolocation("1.98798","2.143")
    assert(Geopolygon(geos).contains(point) == true)
  }
  
  test("inside: ray from point intersects right edge") {
    val geos = Geolocation("8.67","4.5") :: Geolocation("7.78","9.68") :: Geolocation("0.23874","9.00002") :: Geolocation("1.23545","1.234") :: Nil
    val point = Geolocation("1.98798","9.132938")
    assert(Geopolygon(geos).contains(point) == true)
  }
  
  test("outside: point is below polygon") {
    val geos = Geolocation("8.67","4.5") :: Geolocation("7.78","9.68") :: Geolocation("0.23874","9.00002") :: Geolocation("1.23545","1.234") :: Nil
    val point = Geolocation("-3.98","6.132938")
    assert(Geopolygon(geos).contains(point) == false)
  }
  
  test("outside: point is above polygon") {
    val geos = Geolocation("8.67","4.5") :: Geolocation("7.78","9.68") :: Geolocation("0.23874","9.00002") :: Geolocation("1.23545","1.234") :: Nil
    val point = Geolocation("20.98","6.132938")
    assert(Geopolygon(geos).contains(point) == false)
  }
  
  test("inside: using real geo coordinates") {
    //    top-left = 45.543058172101794,-73.59663963317871
    //    top-right = 45.52324728409929,-73.55376720428467
    //    bottom-right = 45.50724918499527,-73.56900215148926
    //    bottom-left = 45.52697551076455,-73.61294746398926
    
    val geos = Geolocation("45.543058172101794","-73.59663963317871") :: 
               Geolocation("45.52324728409929","-73.55376720428467")  :: 
               Geolocation("45.50724918499527","-73.5690021514892")   :: 
               Geolocation("45.52697551076455","-73.6129474639892")   :: Nil
               
    val point1 = Geolocation("45.52643433190143","-73.58076095581055")
    val point2 = Geolocation("45.542396919697666","-73.596510887146")
    
    assert(Geopolygon(geos).contains(point1) == true)
    assert(Geopolygon(geos).contains(point2) == true)
  }
  
  test("outside: using real geo coordinates") {
    //    top-left = 45.543058172101794,-73.59663963317871
    //    top-right = 45.52324728409929,-73.55376720428467
    //    bottom-right = 45.50724918499527,-73.56900215148926
    //    bottom-left = 45.52697551076455,-73.61294746398926
    
    val geos = Geolocation("45.543058172101794","-73.59663963317871") :: 
               Geolocation("45.52324728409929","-73.55376720428467")  :: 
               Geolocation("45.50724918499527","-73.5690021514892")   :: 
               Geolocation("45.52697551076455","-73.6129474639892")   :: Nil
               
    val point1 = Geolocation("45.52883953144113","-73.61410617828369")
    val point2 = Geolocation("45.50724918499527","-73.56840133666992")
    val point3 = Geolocation("45.513324186793014","-73.5944938659668")
    
    assert(Geopolygon(geos).contains(point1) == false)
    assert(Geopolygon(geos).contains(point2) == false)
    assert(Geopolygon(geos).contains(point3) == false)
  }
}