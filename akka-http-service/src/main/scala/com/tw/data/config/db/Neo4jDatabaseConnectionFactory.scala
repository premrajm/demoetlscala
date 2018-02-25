package com.tw.data.config.db
import com.typesafe.config.ConfigFactory
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase, Session}

class Neo4jDatabaseConnectionFactory extends DatabaseConnectionFactory {

  val config = ConfigFactory.load().getConfig("neo4j")
  val driver = GraphDatabase.driver(config.getString("url"), AuthTokens.basic(config.getString("username"),
    config.getString("password")))

  override def getSession(): Session = driver.session()

  override def close(): Unit = driver.close()
}

