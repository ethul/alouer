/**
 * 
 */
package com.alouer.service.util

import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * @author ethul
 *
 */
object TimeAccessor {
  private val pattern = "yyyy.MM.dd HH.mm.ss SSS"
  private val calendar = Calendar getInstance
  private val formatter = new SimpleDateFormat(pattern)
  
  def now() : Long = {
    System currentTimeMillis
  }
  
  def nowString() : String = {
    calendar setTimeInMillis now
    formatter format calendar.getTime
  }
}
