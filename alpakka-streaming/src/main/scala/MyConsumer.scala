import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.ExecutionContext.Implicits.global


object MyConsumer extends App {

  val db = new DB
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()
  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  db.loadOffset().foreach { fromOffset =>
    val partition = 0
    val subscription = Subscriptions.assignmentWithOffset(
      new TopicPartition("test", partition) -> fromOffset
    )
    val done =
      Consumer.plainSource(consumerSettings, subscription)
        .mapAsync(1)(db.save)
        .runWith(Sink.ignore)
  }
}
