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
import person.Gender._
import person.{Gender, Person}

import scala.concurrent.duration._

object ProtobufProducer extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new ByteArraySerializer)
    .withBootstrapServers("localhost:9092")

  val lines: Source[String, NotUsed] = scaladsl.FileTailSource.lines(
    path = FileSystems.getDefault.getPath(getClass.getResource("/data/MOCK_DATA.csv").getPath),
    maxLineSize = 8192,
    pollingInterval = 250.millis
  )

  val stringArrayToPerson = (arr: Array[String]) =>
    Person()
      .withId(arr(Person.ID_FIELD_NUMBER).toInt)
      .withFirstName(arr(Person.FIRSTNAME_FIELD_NUMBER))
      .withEmail(arr(Person.EMAIL_FIELD_NUMBER))
      .withLastName(arr(Person.LASTNAME_FIELD_NUMBER))
      .withGender(arr(Person.GENDER_FIELD_NUMBER) match {
        case "Male" => MALE
        case "Female" => FEMALE
        case _ => NOT_SPECIFIED
      })
      .withIpAddress(arr(Person.IPADDRESS_FIELD_NUMBER))

  private val protobufTopic = "protobuf"
  lines
    .map(line => line.split(','))
    .map(stringArrayToPerson)
    .map(p => new ProducerRecord[Array[Byte], Array[Byte]](protobufTopic, Person.toByteArray(p)))
    .runWith(Producer.plainSink(producerSettings))

}
