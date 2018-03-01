package com.tw.data.repositories.impl

import com.tw.data.repositories.model.Customer

class CustomerRepositoryImplTest extends AbstractRepositoryTest {

  val customerRepository = new CustomerRepositoryImpl(driver)

  test("exists should return true if customer exists"){
    setupSampleData
    val result = customerRepository.exists(2000)
    assertResult(true)(result)
  }

  test("exists should return false if customer does not exist"){
    setupSampleData
    val result = customerRepository.exists(500)
    assertResult(false)(result)
  }

  test("should add new customer"){
    val customer = Customer(2000, "John Doe", 20, "US")
    val result = customerRepository.save(customer)
    assertResult(customer)(result)
  }

  private def setupSampleData: Unit = {
    val session = driver.getSession
    session.run("CREATE (c:Customer {id: 2000, name: 'Hank'})")
    session.close()
  }
}
