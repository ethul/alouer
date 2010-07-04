/**
 * 
 */
package com.alouer.ui

import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.combinator.syntactical.StandardTokenParsers

/**
 * @author ethul
 *
 */
case class CliParser() extends StandardTokenParsers with PackratParsers {
  lexical.reserved ++= List("stats","version","delete","show","help","quit","start")
  
  private[this] def command = 
    stats | version | delete | show | help | quit | start

  private[this] def stats = "stats" ^^ { case _ => Stats }
  private[this] def version = "version" ^^ { case _ => Version }
  private[this] def delete = "delete" ^^ { case _ => Delete }
  private[this] def show = "show" ^^ { case _ => Show }
  private[this] def help = "help" ^^ { case _ => Help }
  private[this] def quit = "quit" ^^ { case _ => Quit }
  private[this] def start = "start"~(numericLit ^^ { _.toInt }) ^^ { case x~y => Start(y) }
  
  def parse(input: String) = {
    val head = phrase(command)
    head(new lexical.Scanner(input)) match {
      case Success(x, _) => x
      case NoSuccess(_, _) => Unknown
    }
  }
}