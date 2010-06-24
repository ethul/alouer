/**
 * 
 */
package com.alouer.service.persistence

/**
 * @author ethul
 *
 */
abstract class Configuration[A,B] 
extends Cache[A,B] 
with Reloadable
