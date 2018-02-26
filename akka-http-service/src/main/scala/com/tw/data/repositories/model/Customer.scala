package com.tw.data.repositories.model

case class Customer(id: Int, name: String, age: Int, country: String)

object Customer {
  val EMPTY: Customer = Customer(-1,null, -1, null)
}
