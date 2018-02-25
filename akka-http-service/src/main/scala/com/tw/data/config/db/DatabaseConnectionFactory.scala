package com.tw.data.config.db

import org.neo4j.driver.v1.Session

trait DatabaseConnectionFactory {

  def getSession(): Session

  def close()

}
