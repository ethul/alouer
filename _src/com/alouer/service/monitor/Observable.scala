/**
 * 
 */
package com.alouer.service.monitor

import scala.collection.mutable.Buffer

/**
 * something which is able to be observed by subscribers and sends
 * notifications to subscribers
 * 
 * @author ethul
 *
 */
trait Observable {
  protected val subscribers: Buffer[Subscribable]
  
  def subscribe(subscriber: Subscribable) {
    subscribers append subscriber
  }
  
  def notify(notification: Notifiable) {
    subscribers foreach { _ notify notification }
  }
}