/**
 * 
 */
package com.alouer.service.monitor

/**
 * something which is able to be subscribed to an observer and
 * receives notifications
 * 
 * @author ethul
 *
 */
trait Subscribable {
  def notify(notification: Notifiable): Unit
}