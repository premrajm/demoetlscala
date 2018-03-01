package com.tw.spark

import org.apache.spark.sql.SparkSession
import org.neo4j.spark._

object CustomerGraphCreation extends App {

  val sparkSession = SparkSession
    .builder()
    .master("local[*]")
    .appName("LoadDataToNeo4j")
    .getOrCreate();

  val sc = sparkSession.sparkContext

  val config = Neo4jConfig("localhost:", "neo4j", Option("root"))
  Neo4j(sc).cypher("CREATE (c:Client {id:1230}) return c").loadRdd
  sparkSession.close()
}
