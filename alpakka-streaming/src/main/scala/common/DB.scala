package common

import java.util.concurrent.atomic.AtomicLong

import akka.Done
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.concurrent.Future

class DB {

  private val offset = new AtomicLong

  def save[T](record: ConsumerRecord[_, T])(f: T => String): Future[Done] = {
    println(s"DB.save: ${f(record.value)}")
    offset.set(record.offset)
    Future.successful(Done)
  }

  def loadOffset(): Future[Long] = Future.successful(offset.get)

  def update(data: String): Future[Done] = {
    println(s"DB.update: $data")
    Future.successful(Done)
  }
}
