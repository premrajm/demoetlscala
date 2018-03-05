package avro

import java.io.ByteArrayOutputStream
import java.nio.file.FileSystems

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.alpakka.file.scaladsl
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.io.{BinaryEncoder, EncoderFactory}
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.duration._

object AvroProducer extends App {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()

  val producerSettings = ProducerSettings(system, new StringSerializer, new ByteArraySerializer)
    .withBootstrapServers("localhost:9092")

  val lines: Source[String, NotUsed] = scaladsl.FileTailSource.lines(
    path = FileSystems.getDefault.getPath(getClass.getResource("/data/MOCK_DATA.csv").getPath),
    maxLineSize = 8192,
    pollingInterval = 250.millis
  )

  val schema: Schema =
    new Schema.Parser().parse(scala.io.Source.fromURL(getClass.getResource("/avroSchema/person.avsc")).mkString)

  val stringArrayToGenericRecord = (arr: Array[String]) => {
    val person: GenericRecord = new GenericData.Record(schema)
    person.put("id", arr(0).toInt)
    person.put("firstName", arr(1))
    person.put("lastName", arr(2))
    person.put("email", arr(3))
    person.put("gender", arr(4))
    person.put("ipAddress", arr(5))
    person
  }

  val genericRecordToByteArray = (person: GenericRecord) => {
    val writer = new SpecificDatumWriter[GenericRecord](schema)
    val out = new ByteArrayOutputStream()
    val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
    writer.write(person, encoder)
    encoder.flush()
    out.close()
    out.toByteArray
  }

  private val avroTopic = "avro"
  lines
    .map(line => line.split(','))
    .map(stringArrayToGenericRecord)
    .map(genericRecordToByteArray)
    .map(p => new ProducerRecord[String, Array[Byte]](avroTopic, p))
    .runWith(Producer.plainSink(producerSettings))
}
