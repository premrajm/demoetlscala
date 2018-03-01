package com.tw.data.generator

import scala.util.Random

object ExampleData {
  case class Property(name:String, value:String)
  case class Node(name:String, properties:List[Property]) {
    def propertyJson = {
      val pjson = properties.map(p⇒ s"${p.name}:'${p.value}',")
      "{" + pjson.reduce((first, second) ⇒ first + second).dropRight(1) + "}"
    }
  }

  case class Relation(name:String, properties:List[Property], from:Node, to:Node)

// TODO: Instead of picking random names, may be use names from standard set of names
//  val relationships = List("ASSIGNED_TO", "WORKING_FOR", "HAS", "BOOKED", "MEMBEROF", "BANKEROF", "CLIENTOF", "WORKS_FOR", "FAMILY_OF", "CLIENT_OF")
//  val propertiesPrefixes = List("name", "address", "pincode", "accountnumber", "address", "zipcode")
//  val names = List("Bill", "Jim")

  def getRandomString() = {
    Random.alphanumeric.filter(!_.isDigit).take(10).mkString
  }


  def getRandomProperties(count:Int = 50): Seq[Property] = {
      val range = 0 until count
      range.map(index ⇒ Property(getRandomString(), getRandomString()))
  }

  def generateRandomNodes(count:Int = 100): Seq[Node] = {
    val range = 0 until count
    range.map(index ⇒ Node(getRandomString(), getRandomProperties().toList))
  }

  def randomNodesCypher(noOfNodes:Int = 100) = {
    val cypher = "CREATE "
    val nodes = generateRandomNodes(noOfNodes)
    val nodesCypher: Seq[String] = nodes.map(node ⇒ s"( ${node.name.toLowerCase}:${node.name.toUpperCase}${node.propertyJson}),\n")
    cypher + " " + nodesCypher.reduce((first, second)⇒ first + second).dropRight(2)
  }
}

object DataMain extends App {
  println(ExampleData.randomNodesCypher())
}