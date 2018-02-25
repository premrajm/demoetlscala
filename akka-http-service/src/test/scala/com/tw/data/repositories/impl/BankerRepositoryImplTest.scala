package com.tw.data.repositories.impl

import org.scalatest.{BeforeAndAfterAll, FunSuite}

class BankerRepositoryImplTest extends FunSuite with BeforeAndAfterAll {

  val testDriver = new TestNeo4jConnectionFactory

  val bankerRepository = new BankerRepositoryImpl(testDriver)

  override protected def beforeAll(): Unit = {

  }

  override protected def afterAll(): Unit = {
    val session = testDriver.getSession()
    session.run("MATCH (n) DETACH DELETE (n)")
    session.close()
  }

  test("bankerExists should return true for valid banker") {
    setupSampleData
    assertResult(true)(bankerRepository.bankerExists(100))
  }

  test("bankerExists should return false for valid banker") {
    setupSampleData
    assertResult(false)(bankerRepository.bankerExists(200))
  }

  private def setupSampleData = {
    val session = testDriver.getSession()
    session.run("CREATE (b:Banker {id: 100, name: 'Tom'})")
    session.close()
  }

}
