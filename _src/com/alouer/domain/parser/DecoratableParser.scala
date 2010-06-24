/**
 * 
 */
package com.alouer.domain.parser

/**
 * @author ethul
 *
 */
trait DecoratableParser {
  protected[this] def parser(): RssParsable
}
