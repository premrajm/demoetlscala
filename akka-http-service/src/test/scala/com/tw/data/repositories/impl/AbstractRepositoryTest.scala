package com.tw.data.repositories.impl

import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}

abstract class AbstractRepositoryTest extends FunSuite with BeforeAndAfterEach with Matchers{

  val driver = new TestNeo4jConnectionFactory

  override protected def beforeEach(): Unit = {
    super.beforeEach()
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    val session = driver.getSession
    session.run("MATCH (n) DETACH DELETE (n)")
    session.close
  }
}
