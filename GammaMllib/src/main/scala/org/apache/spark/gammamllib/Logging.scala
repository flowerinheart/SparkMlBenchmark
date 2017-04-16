package org.apache.spark.gammamllib

/**
  * Created by Darnell on 2017/4/11.
  */
import org.slf4j.LoggerFactory
trait Logging {
  lazy val logger = LoggerFactory.getLogger(this.getClass.getName.stripSuffix("$"))
  def warn(s : String) = logger.warn(s)
  def debug(s : String) = logger.debug(s)

}
