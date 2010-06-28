/**
 * 
 */
package com.alouer.domain.util

import com.alouer.service.monitor.{Notifiable,Observable,Subscribable}
import com.alouer.service.util.{Logger,Statistics}
import scala.collection.mutable.ListBuffer
import scala.math.BigDecimal
import java.math.MathContext

/**
 * @author ethul
 * 
 */
case class Geopolygon(geopoints: List[Geolocatable]) extends Observable {
  private[this] val infolog = Logger.log(Logger.Info) _
  protected val subscribers = ListBuffer[Subscribable]()
  
  /**
   * map the geopoints to our internal point representation
   * longitude is the x-coordinate
   * latitude is the y-coordinate
   */
  private[this] val points = geopoints.map {
    a => Point(a)
  }
  
  /**
   * retain all points which are in this polygon, or which have
   * an unknown geolocatable 
   * @param points the points to check
   * @return a list of points retained
   */
  def retain(points: List[Geolocatable]): List[Geolocatable] = {
    val retained = points.filter(contains _)
    notify(Statistics.GeopolygonAccumulator(retained length))
    retained
  }
  
  def contains(point: Geolocatable): Boolean = {
    // ray casting algorithm which draws a ray increasing
    // in the y-coordinate direction and for each edge of
    // the polygon, tallys the number of times the point
    // intersects. if the number of times is odd, then the
    // polygon contains the point, otherwise it does not
    infolog("checking geolocation: " + point)
    
    // the point we are checking if it is contained
    val p = Point(point)
    var inside = false
    
    // iterate on each edge of the polygon defined as
    for (i <- 0 to points.length - 1) {
      val (x0,y0,x1,y1) = reorder(i)
      // only test for intersection when the x-coordinate
      // of p lies between the left and right x-coordinate
      // of the current edge
      if (x0 < p.x && p.x < x1) {
        // find the slope of the edge
        val m = (y1 - y0) / (x1 - x0)
        
        // given the slope and the starting point of the 
        // edge derive the equation of the line using
        // the definition of slope
        //   m * (x1 - x0) = (y1 - y0)
        //   m * (x1 - x0) + y0 = y1
        // 
        // the y1 is y we want to solve for by
        // plugging in the p.x for x1
        val y = m * (p.x - x0) + y0
        
        // when the p.y is below the derived y, the ray
        // will intersect the edge
        if (p.y < y) {
          infolog("ray cast intersects: ("+x0+","+y0+"),("+x1+","+y1+")")
          inside = !inside
        }
      }
    }
    
    inside
  }
  
  /**
   * ensures that x0 < x1
   * @param i
   * @return (x0,y0,x1,y1)
   */
  private[this] def reorder(i: Int) = {
    if (points(i).x < points((i+1) % points.length).x) {
      (
         points(i).x,
         points(i).y,
         points((i+1) % points.length).x,
         points((i+1) % points.length).y
      )
    }
    else {
      (
         points((i+1) % points.length).x,
         points((i+1) % points.length).y,
         points(i).x,
         points(i).y
      )
    }
  }
  
  private[this] case class Point(g: Geolocatable) {
    private[this] val PRECISION = 100
    val x = BigDecimal(g.longitude, new MathContext(PRECISION))
    val y = BigDecimal(g.latitude, new MathContext(PRECISION))
    override def toString(): String = "("+x+","+y+")"
  }
}

