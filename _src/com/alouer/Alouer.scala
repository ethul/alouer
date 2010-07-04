/**
 * 
 */
package com.alouer

import com.alouer.domain.AlouerFacade
import com.alouer.ui._
import com.alouer.service.util.{Logger,TimeAccessor,Statistics}
import scala.actors.Actor._
import java.io.FileWriter
import java.util.concurrent.{Executors,TimeUnit}

/**
 * @author ethul
 *
 */
object Alouer {
  private[this] val version = "1.2"
    
  def main(args: Array[String]) {
    val infolog = Logger.log(Logger.Info) _
    val warnlog = Logger.log(Logger.Warning) _
    val scheduler = Executors.newSingleThreadScheduledExecutor
    val parser = CliParser()
    val facade = AlouerFacade()
    
    val cli = actor {
      loop {
        react {
          case Unknown => println("unknown command, try help")
          case Stats => println(Statistics)
          case Version =>  println(version)
          case Delete => facade deleteMapItems
          case Show => facade showMapItems
          
          case Start(x) => {
            scheduler.scheduleWithFixedDelay(new Runnable {
              def run() {
                  Statistics.reset
                  facade.createMapItems
                  infolog("\n" + Statistics)
              }
            }, 5L, x * 60L, TimeUnit.SECONDS)
            println("task scheduled every " + x + " minutes")
          }
          
          case Quit => {
            scheduler.shutdown
            if (!scheduler.awaitTermination(120L, TimeUnit.SECONDS)) {
              println("cannot schedule, previous task not shutdown")
              println("forcing shutdown")
              scheduler.shutdownNow
            }
            Logger.close
            exit
          }
          
          case Help => {
            println("available commands")
            println("help: displays this message")
            println("start: creates and sets up a recurring task")
            println("show: show all map items")
            println("delete: removes all map items")
            println("stats: shows current statistics")
            println("version: shows the current version")
            println("quit: shuts down any tasks and terminates the program")
          }
          
          case Ready => {
            print("alouer$ ")
            self ! parser.parse(Console.readLine)
            self ! Ready
          }
        }
      }
    }
    
    facade.initialize
    cli ! Ready
  }
}