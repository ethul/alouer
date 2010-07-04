/**
 * 
 */
package com.alouer.service.util

import java.io.{ByteArrayOutputStream,FileWriter,PrintStream}

/**
 * @author ethul
 *
 */
object Logger {
  private[this] val logfile = "/home/ethul/tmp/alouer.log"
  private[this] val appender = new FileWriter(logfile, true)

  def log(level: Level)(content: Any) {
    val formatted = content match {
      case e: Exception => {
        val buffer = new ByteArrayOutputStream
        e.printStackTrace(new PrintStream(buffer))
        buffer.toString
      }
      case s: String => s
    }
    appender.write(level.prefix + formatted)
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
