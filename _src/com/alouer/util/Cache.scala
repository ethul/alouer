/**
 * 
 */
package com.alouer.util

import scala.collection.mutable.Map
import scala.io.BufferedSource
import java.io.{File,FileInputStream,FileWriter}

/**
 * @author ethul
 *
 */
trait Cache {
  def contains(key: String): Boolean
  def get(key: String): Option[String]
  def put(key: String, value: String): Boolean
}

class FileCache(path: String) extends Cache {
  private[this] val infolog = Logger.log(Logger.Info) _
  private[this] val separator = " "
  private[this] val cache: Map[String,String] = Map()
  load()
  
  def contains(key: String): Boolean = cache.contains(key)
  def get(key: String): Option[String] = cache.get(key)
  def put(key: String, value: String): Boolean = {
    infolog("putting into cache: " + key + "," + value)
    cache += (key -> value)
    val output = new FileWriter(path)
    cache.foreach { a =>
      store(output, a._1, a._2)
    }
    output.close
    true
  }
  
  private[this] def load() {
    val file = new File(path)
    if (!file.exists) {
      file.createNewFile
    }
    val source = new BufferedSource(new FileInputStream(file))
    source.getLines().foreach { a =>
      val split = a.split(separator)
      val key = split(0)
      val value = split(1)
      cache += (key -> value)
    }
    source.close
  }
  
  private[this] def store(output: FileWriter, key: String, value: String) {
    val builder = new StringBuilder
    builder append(key) append(separator) append(value)
    output.write(builder.mkString)
    output.write('\n')
  }
}