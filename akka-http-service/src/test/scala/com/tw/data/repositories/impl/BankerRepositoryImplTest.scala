package com.tw.data.repositories.impl

import org.scalatest.{BeforeAndAfterAll, FunSuite}

class BankerRepositoryImplTest extends AbstractRepositoryTest {

  val bankerRepository = new BankerRepositoryImpl(driver)

  test("bankerExists should return true for valid banker") {
    setupSampleData
    assertResult(true)(bankerRepository.bankerExists(100))
  }

  test("bankerExists should return false for valid banker") {
    setupSampleData
    assertResult(false)(bankerRepository.bankerExists(200))
  }

  private def setupSampleData() = {
    val session = driver.getSession
    session.run("CREATE (b:Banker {id: 100, name: 'Tom'})")
    session.close()
  }

}
