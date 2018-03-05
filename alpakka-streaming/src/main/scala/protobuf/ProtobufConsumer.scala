package protobuf

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import common.DB
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import person.Person

import scala.concurrent.ExecutionContext.Implicits.global

object ProtobufConsumer extends App {

  val db = new DB
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()
  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new ByteArrayDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  db.loadOffset().foreach { fromOffset =>
    val partition = 0
    val protobufTopic = "protobuf"
    val subscription = Subscriptions.assignmentWithOffset(
      new TopicPartition(protobufTopic, partition) -> fromOffset
    )

    def byteArrayToPerson(arr: Array[Byte]): String = Person.parseFrom(arr).toString

    val done =
      Consumer
        .plainSource(consumerSettings, subscription)
        .mapAsync(1)(r => db.save(r)(byteArrayToPerson))
        .runWith(Sink.ignore)
  }

}
