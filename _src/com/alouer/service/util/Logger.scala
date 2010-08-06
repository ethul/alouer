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
  private[this] var appender: FileWriter = _
  
  def initialize(file: String) {
    appender = new FileWriter(file, true)
  }
  
  /**
   * <p>
   * the apply of the function this method returns depends on
   * the type class LogFormatter. the point of this class is
   * to apply the correct format method based on the type of
   * A which is passed to be logged
   * 
   * <p>
   * thanks to the clever users of the scala-user mailing list
   * i have an answer for allowing for what appears to be a
   * partially applied function. but really one function is
   * returning another which will be executed later
   */
  def log(level: Level) = new {
    def apply[A: LogFormatter](a: A) {
      val formatted = implicitly[LogFormatter[A]].format(a)
      appender.write(level.prefix + formatted)
      appender.write('\n')
      appender.flush
    }
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
