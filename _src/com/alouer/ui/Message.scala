/**
 * 
 */
package com.alouer.ui

/**
 * @author ethul
 *
 */
sealed trait Message
case object Ready extends Message
case object Stop extends Message
case object Stats extends Message
case object Version extends Message
case object Delete extends Message
case object Show extends Message
case object Help extends Message
case object Quit extends Message
case object Unknown extends Message
case class Start(frequency: Int) extends Message
