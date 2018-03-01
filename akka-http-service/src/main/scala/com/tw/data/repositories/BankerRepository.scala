package com.tw.data.repositories

trait BankerRepository {

  def bankerExists(id: Int): Boolean

}
