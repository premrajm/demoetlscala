package protobuf

import java.nio.file.FileSystems

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.alpakka.file.scaladsl
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import person.Person

import scala.concurrent.duration._

object ProtobufProducer extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new ByteArraySerializer)
    .withBootstrapServers("localhost:9092")

  val lines: Source[String, NotUsed] = scaladsl.FileTailSource.lines(
    path = FileSystems.getDefault.getPath("/Users/premrajm/Documents/Projects/MOCK_DATA.csv"),
    maxLineSize = 8192,
    pollingInterval = 250.millis
  )

  val stringArrayToPerson = (arr: Array[String]) =>
    Person()
      .withId(arr(0).toInt)
      .withFirstName(arr(1))
      .withEmail(arr(2))
      .withLastName(arr(3))
      .withGender(arr(4))
      .withIpAddress(arr(5))

  lines
    .map(line => line.split(','))
    .map(stringArrayToPerson)
    .map(p => new ProducerRecord[Array[Byte], Array[Byte]]("protobuf", Person.toByteArray(p)))
    .runWith(Producer.plainSink(producerSettings))

}
