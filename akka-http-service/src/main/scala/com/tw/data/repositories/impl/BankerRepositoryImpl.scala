package com.tw.data.repositories.impl

import com.google.inject.Inject
import com.tw.data.config.db.DatabaseConnectionFactory
import com.tw.data.repositories.BankerRepository

class BankerRepositoryImpl @Inject()(val dbConnection: DatabaseConnectionFactory)
    extends BankerRepository {

  override def bankerExists(id: Int): Boolean = {
    var isExist = false
    val session = getSession
    val result  = session.run(s"MATCH (b:Banker) WHERE b.id = ${id} RETURN b.name")
    session.close()
    if (result.hasNext)
      isExist = true

    isExist
  }

  private def getSession = {
    dbConnection.getSession
  }
}
