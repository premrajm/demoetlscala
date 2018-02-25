package com.tw.data.repositories.impl

import com.tw.data.config.db.DatabaseConnectionFactory
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase, Session}

class TestNeo4jConnectionFactory extends DatabaseConnectionFactory{

  val driver =  GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j","root"))

  override def getSession(): Session = driver.session()

  override def close(): Unit = driver.close()
}
