package com.tw.data.repositories.impl

import com.google.inject.Inject
import com.tw.data.config.db.DatabaseConnectionFactory
import com.tw.data.repositories.model.Customer

case class CustomerRepositoryImpl @Inject()(driver: DatabaseConnectionFactory) {

  def exists(id: Int): Boolean = {
    val session = driver.getSession
    var isExists = false
    try {
      val result = session.run(s"MATCH (c:Customer) WHERE c.id = ${id} RETURN c")
      if (result.hasNext) {
        isExists = true
      }
    } finally {
      session.close()
    }
    isExists
  }

  def save(customer: Customer): Customer = {
    val session = driver.getSession
    var executionResult: Customer = Customer.EMPTY
    try {
      val result = session.run(
        s"CREATE (c:Customer {id: ${customer.id}," +
          s" name: '${customer.name}', age: ${customer.age}, " +
          s"country: '${customer.country}'}) RETURN properties(c)")
      if (result.hasNext) {
        executionResult = customer
      }
    } finally {
      session.close()
    }
    executionResult
  }

}
