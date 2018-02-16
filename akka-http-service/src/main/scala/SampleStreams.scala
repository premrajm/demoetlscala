import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

object SampleStreams extends App {
  implicit val system: ActorSystem = ActorSystem("StreamServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  Source(1 to 1000)
    .runWith(Sink.foreach(i ⇒ println(i)))

  system.terminate()

}
