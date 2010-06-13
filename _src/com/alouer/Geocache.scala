/**
 * 
 */
package com.alouer

import scala.io.BufferedSource
import java.io.{File,FileInputStream,FileWriter}

/**
 * @author ethul
 *
 */
abstract class Geocache {
  type T = String
  def contains(key: T): Boolean
  def get(key: T): Option[Geolocation]
  def put(key: T, value: Geolocation): Boolean
}

class FileGeocache(filepath: String) extends Geocache {
  private[this] val separator = "="
  private[this] var cache = load()
  
  def contains(key: T): Boolean = cache.contains(key)
  def get(key: T): Option[Geolocation] = cache.get(key)
  def put(key: T, value: Geolocation): Boolean = {
    store(key, value)
    cache = load()
    true
  }
  
  private[this] def load(): Map[String, Geolocation] = {
    val file = new File(filepath)
    if (!file.exists) {
      file.createNewFile
    }
    val source = new BufferedSource(new FileInputStream(file))
    val map = source.getLines().map { a =>
      val split = a.split('=')
      val locations = split(1).split(',')
      (split(0), Geolocation(locations(0), locations(1)))
    }.toMap
    source.close
    map
  }
  
  private[this] def store(key: T, value: Geolocation) {
    val output = new FileWriter(filepath, true)
    val builder = new StringBuilder
    builder append(key) append(separator) append(value)
    output.write(builder.mkString)
    output.write('\n')
    output.close
  }
}