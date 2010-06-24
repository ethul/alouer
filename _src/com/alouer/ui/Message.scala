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
case class Input(input: String) extends Message