package com.raft.core

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object FutureApp extends App {
  import scala.concurrent.ExecutionContext.Implicits.global

  private val eventualUnit = Future {
    println("Hello from future")
  }
  eventualUnit.onComplete(_ ⇒ println("Done"))
  Await.result(eventualUnit, 5 seconds)

}
