/**
 * 
 */
package com.alouer.domain.parser

import com.alouer.domain.parser.impl.SimpleRssItem
import com.alouer.service.util.{Logger,UrlConnectable}
import scala.xml.{Elem,Node,XML}

/**
 * @author ethul
 *
 */
abstract class AbstractParser(feed: String)
extends RssParsable with UrlConnectable {
  private[this] val infolog = Logger.log(Logger.Info) _
  private[this] val error = Logger.log(Logger.Error) _
  
  def parse(): List[RssItemizable] = {
    infolog("loading rss feed: " + feed)
    val items = try {
      val rss = XML.load(connection(feed))
      rss \\ "item"
    }
    catch {
      case e: Exception => {
        error(e)
        Nil
      }
    }
    
    items.map { a =>
      val link = (a \ "link").text
      val title = (a \ "title").text
      val description = (a \ "description").text
      val date = parseDate(a)
      val address = parseAddress(link)
      SimpleRssItem(
        link,
        removeQuotes(title),
        removeQuotes(removeNonPrintables(description)),
        address,
        date
      )
    }.toList
  }
  
  protected def parseAddress(link: String): String
  
  protected def parseDate(node: Node): String = {
    val i = node.child.indexWhere(_.label == "date")
    node.child(i).text
  }
  
  protected final def removeNonPrintables(value: String) = {
    value.filter(a => a >= ' ').toString
  }
  
  protected final def removeQuotes(value: String) = {
    value.replace("\"", "").replace("'", "").replace("`","")
  }
  
  /**
   * Copyright (c) 2008 Florian Hars, BIK Aschpurwis+Behrens GmbH, Hamburg 
   * Copyright (c) 2002-2008 EPFL, Lausanne, unless otherwise specified. 
   * All rights reserved. 
   *
   * This software was developed by the Programming Methods Laboratory of the 
   * Swiss Federal Institute of Technology (EPFL), Lausanne, Switzerland. 
   *
   * Permission to use, copy, modify, and distribute this software in source 
   * or binary form for any purpose with or without fee is hereby granted, 
   * provided that the following conditions are met: 
   *
   *  1. Redistributions of source code must retain the above copyright 
   *     notice, this list of conditions and the following disclaimer. 
   *
   *  2. Redistributions in binary form must reproduce the above copyright 
   *     notice, this list of conditions and the following disclaimer in the 
   *     documentation and/or other materials provided with the distribution. 
   *
   *  3. Neither the name of the EPFL nor the names of its contributors 
   *     may be used to endorse or promote products derived from this 
   *     software without specific prior written permission. 
   *
   *
   * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND 
   * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
   * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
   * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE 
   * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
   * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
   * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
   * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
   * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY 
   * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF 
   * SUCH DAMAGE. 
   */ 
  import org.xml.sax.InputSource 
  import javax.xml.parsers.SAXParser 
  import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl 
  import scala.xml.parsing.FactoryAdapter 
  import scala.xml._ 

  protected class TagSoupFactoryAdapter extends FactoryAdapter { 
    private[this] val parserFactory = new SAXFactoryImpl 
    parserFactory.setNamespaceAware(false) 

    private[this] val emptyElements = 
      Set("area", "base", "br", "col", "hr", "img", "input", "link", "meta", "param") 

    /** 
     * Tests if an XML element contains text. 
     * @return true if element named <code>localName</code> contains text. 
     */ 
    def nodeContainsText(localName: String) = !(emptyElements contains localName) 

   /** 
    * creates a node. 
    */ 
    def createNode(pre: String, label: String, attrs: MetaData, 
                   scpe: NamespaceBinding, children: List[Node]) = Elem(pre, label, attrs, scpe, children:_*); 

    /** 
     * creates a text node 
     */ 
    def createText( text:String ) = Text( text ); 

    /** 
     * Ignore Processing Instructions 
     */ 
    def createProcInstr(target: String, data: String) = Nil 

    /** 
     * load XML document 
     * @param source 
     * @param parser
     * @return a new XML document object 
     */ 
    override def loadXML(source: InputSource, parser: SAXParser) = { 
      val parser: SAXParser = parserFactory.newSAXParser() 
      scopeStack.push(TopScope) 
      parser.parse(source, this) 
      scopeStack.pop 
      rootElem 
    } 
  } 
}