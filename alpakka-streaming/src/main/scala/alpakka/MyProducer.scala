package alpakka

import java.nio.file.FileSystems

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.alpakka.file.scaladsl
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.duration._

object MyProducer extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")
  val lines: Source[String, NotUsed] = scaladsl.FileTailSource.lines(
    path = FileSystems.getDefault.getPath("/Users/premrajm/Documents/Projects/MOCK_DATA.csv"),
    maxLineSize = 8192,
    pollingInterval = 250.millis
  )

  lines
    .map { line =>
      new ProducerRecord[Array[Byte], String]("test", line)
    }
    .runWith(Producer.plainSink(producerSettings))
}
