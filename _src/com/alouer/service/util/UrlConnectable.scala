/**
 * 
 */
package com.alouer.service.util

import java.io.InputStream
import java.net.{URL,URLConnection}

/**
 * @author ethul
 *
 */
trait UrlConnectable {
  private[this] val timeout = 45 * 1000
  private[this] val acceptLanguage = ("Accept-Language", "en-CA")
  protected def connection(url: String): InputStream = {
    val connection = new URL(url).openConnection
    connection.setRequestProperty(acceptLanguage._1, acceptLanguage._2)
    connection.setConnectTimeout(timeout)
    connection.setReadTimeout(timeout)
    connection.getInputStream
  }
}