/**
 * 
 */
package com.alouer

import com.alouer.domain.AlouerFacade
import com.alouer.service.persistence.Configuration
import com.alouer.service.util.{Logger,TimeAccessor,Statistics}
import com.alouer.ui._
import scala.actors.Actor._
import java.util.concurrent.{Executors,TimeUnit}

/**
 * @author ethul
 *
 */
object Alouer {
  private[this] val version = "1.6"
  private[this] val fallback = "alouer.config"
    
  def main(args: Array[String]) {
    val config = if (args.length > 0) {
      args(0)
    }
    else {
      println("no configuration file specified, using " + fallback)
      fallback
    }
    Configuration.load(config, "=")
    Logger.initialize(Configuration.get("log.file").get)
    
    val info = Logger.log(Logger.Info)
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
                  info("\n" + Statistics)
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