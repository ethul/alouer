/**
 * 
 */
package com.alouer.service.persistence

import com.alouer.service.util.Logger
import scala.collection.mutable.Map
import scala.io.BufferedSource
import java.io.{File,FileInputStream,FileWriter}

/**
 * @author ethul
 *
 */
case class FileCache[A <: String, B <: String](path: String, separator: String) 
extends Cache[A,B] {
  private[this] val infolog = Logger.log(Logger.Info)
  private[this] val cache: Map[A,B] = Map()
  load()
  
  def contains(key: A): Boolean = cache.contains(key)
  def get(key: A): Option[B] = cache.get(key)
  def put(key: A, value: B): Boolean = {
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
      val key = split.head.asInstanceOf[A]
      val value = split.tail.mkString(separator).asInstanceOf[B]
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