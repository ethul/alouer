/**
 * 
 */
package com.alouer.service.util

import java.security.MessageDigest

/**
 * @author ethul
 *
 */
object Md5 {
  /**
   * adapted from
   * http://code-redefined.blogspot.com/2009/05/md5-sum-in-scala.html
   */
  def sum(bytes: String): String = {
    val md5 = MessageDigest.getInstance("MD5")
    md5.reset()
    md5.update(bytes.toCharArray.map(_.asInstanceOf[Byte]))
    md5.digest().map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
  }
}