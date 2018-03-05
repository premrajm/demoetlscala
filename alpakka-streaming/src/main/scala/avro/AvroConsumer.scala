package avro

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import common.DB
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.{DatumReader, Decoder, DecoderFactory}
import org.apache.avro.specific.SpecificDatumReader
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.ExecutionContext.Implicits.global

object AvroConsumer extends App {
  val db = new DB
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()
  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new ByteArrayDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("group1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val schema: Schema =
    new Schema.Parser().parse(scala.io.Source.fromURL(getClass.getResource("/avroSchema/person.avsc")).mkString)

  db.loadOffset().foreach { fromOffset =>
    val partition = 0
    val avroTopic = "avro"
    val subscription = Subscriptions.assignmentWithOffset(
      new TopicPartition(avroTopic, partition) -> fromOffset
    )

    def recordToString(record: Array[Byte]): String = {
      val reader: DatumReader[GenericRecord] = new SpecificDatumReader[GenericRecord](schema)
      val decoder: Decoder = DecoderFactory.get().binaryDecoder(record, null)
      val userData: GenericRecord = reader.read(null, decoder)
      userData.toString
    }

    val done =
      Consumer
        .plainSource(consumerSettings, subscription)
        .mapAsync(1)(r => db.save(r)(recordToString))
        .runWith(Sink.ignore)
  }
}
