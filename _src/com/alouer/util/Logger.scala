/**
 * 
 */
package com.alouer.util

import java.io.FileWriter

/**
 * @author ethul
 *
 */
object Logger {
  private[this] val logfile = "/home/ethul/tmp/alouer.log"
  private[this] val appender = new FileWriter(logfile, true)

  def log(level: Level)(content: String) {
    appender.write(level.prefix + content)
    appender.write('\n')
    appender.flush
  }
  
  def close() {
    appender.close
  }

  sealed abstract class Level {
    def prefix(): String
  }

  case object Error extends Level {
    def prefix(): String = {
      TimeAccessor.nowString + " <error> "
    }
  }

  case object Warning extends Level {
    def prefix(): String = {
      TimeAccessor.nowString + " <warning> "
    }
  }

  case object Info extends Level {
    def prefix(): String = {
      TimeAccessor.nowString + " <info> "
    }
  }
}
