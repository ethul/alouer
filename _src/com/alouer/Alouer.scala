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
  private[this] val maxGeoLookups = 2000
    
  def main(args: Array[String]) {
    val infolog = Logger.log(Logger.Info) _
    val warnlog = Logger.log(Logger.Warning) _
    val scheduler = Executors.newSingleThreadScheduledExecutor
    val facade = AlouerFacade()
    
    val cli = actor {
      loop {
        react {
          case Input("help") => {
            println("available commands")
            println("help: displays this message")
            println("schedule: creates and sets up a recurring task")
            println("delete: removes all map items")
            println("stats: shows current statistics")
            println("quit: shuts down any tasks and terminates the program")
          }
          case Input("quit") => {
            scheduler.shutdown
            if (!scheduler.awaitTermination(120L, TimeUnit.SECONDS)) {
              println("cannot schedule, previous task not shutdown")
              println("forcing shutdown")
              scheduler.shutdownNow
            }
            Logger.close
            exit
          }
          case Input("stats") => {
            println(Statistics)
          }
          case Input("delete") => {
            facade deleteMapItems
          }
          case Input("schedule") => {
            scheduler.scheduleWithFixedDelay(new Runnable {
              def run() {
                if (Statistics.geolookups > maxGeoLookups) {
                  warnlog(Statistics.geolookups + " is over the max number of geo lookups")
                  warnlog("forcing shutdown at " + TimeAccessor.nowString)
                  scheduler.shutdownNow
                }
                else {
                  Statistics.reset
                  facade.createMapItems
                  infolog("\n" + Statistics)
                }
              }
            }, 5L, 60L * 30L, TimeUnit.SECONDS)
            
            println("task scheduled every half hour")
          }
          case Input(input) => {
            println("unknown command: " + input)
            println("try the help command")
          }
          case Ready => {
            print("alouer$")
            self ! Input(Console.readLine) 
            self ! Ready
          }
        }
      }
    }
    
    cli ! Ready
  }
}