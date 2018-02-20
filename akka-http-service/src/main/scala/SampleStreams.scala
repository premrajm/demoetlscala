import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

object SampleStreams extends App {
  def spin(value: Int): Int = {
    val start = System.currentTimeMillis()
    while ((System.currentTimeMillis() - start) < 10) {}
    value
  }

  implicit val system: ActorSystem = ActorSystem("StreamServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  Source(1 to 1000)
    .map(spin)
    .async
    .map(spin)
    .runWith(Sink.ignore)

  system.terminate()

}
