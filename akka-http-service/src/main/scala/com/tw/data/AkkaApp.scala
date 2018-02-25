package com.tw.data

import com.google.inject.{AbstractModule, Guice, Scopes}
import com.tw.data.config.db.{DatabaseConnectionFactory, Neo4jDatabaseConnectionFactory}
import com.tw.data.repositories.BankerRepository
import com.tw.data.repositories.impl.BankerRepositoryImpl

class AkkaApp extends AbstractModule{
  override def configure(): Unit = {
    bind(classOf[DatabaseConnectionFactory]).to(classOf[Neo4jDatabaseConnectionFactory])
      .in(Scopes.SINGLETON)
    bind(classOf[BankerRepository]).to(classOf[BankerRepositoryImpl])
  }

}

object AppStart {
  def main(args: Array[String]): Unit = {
    val injector = Guice.createInjector(new AkkaApp)
    val repository = injector.getInstance(classOf[BankerRepository])
    val isExist = repository.bankerExists(100)
    println(isExist)

    val factory = injector.getInstance(classOf[DatabaseConnectionFactory])
    factory.close()
  }
}
