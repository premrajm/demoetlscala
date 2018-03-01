package com.tw.data.generator

import org.neo4j.driver.v1.{AuthTokens, GraphDatabase}

object Neo4jDataGenerator extends App {

  val driver = GraphDatabase.driver("bolt://localhost:7687/", AuthTokens.basic("neo4j", "password"))
  val connection = driver.session()
  connection.run(ExampleData.randomNodesCypher(noOfNodes = 100)) //100 random nodes, each with 50 properties
  connection.run(ExampleData.randomNodesCypher(noOfNodes = 100))
  connection.run(ExampleData.randomNodesCypher(noOfNodes = 100))
  connection.run(ExampleData.randomNodesCypher(noOfNodes = 100))
  connection.run(ExampleData.randomNodesCypher(noOfNodes = 100))
  connection.close()
  driver.close()
}
