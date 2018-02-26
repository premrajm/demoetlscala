package com.tw.data.service

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.neo4j.driver.v1._
import org.neo4j.driver.v1.util.Pair
import play.api.libs.json.Json

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.io.StdIn

object BankerService extends App with PlayJsonSupport {

  val config = ConfigFactory.load().getConfig("neo4j")

  implicit val system           = ActorSystem("my-actors")
  implicit val materializer     = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  final case class Client(bankerId: Int, id: Int, name: String, age: Int) {
    require(bankerId != null || bankerId <= 0, "Banker Id is mandatory")
  }

  final object Client {
    implicit val clientFormat   = Json.format[Client]
    implicit val responseFormat = Json.format[GetClientsResponse]
  }

  final case class GetClientsResponse(clients: List[Client])

  // driver is a thread-safe instance as per documentation so can be shared globally
  val driver = GraphDatabase.driver(
    config.getString("url"),
    AuthTokens.basic(config.getString("username"), config.getString("password")))

  // TODO: fetch this out my ide is giving error for Neo4jConnection class on compile
  private def connection() = driver.session()

  // for this to run make sure you have a banker created with same id as you pass in the post body
  def addClient(client: Client): Future[Done] = {
    val session: Session = connection
    // Todo: can this be better handled using OGM ?
    val data = session.run(
      s"MATCH (b:Banker {id: ${client.bankerId}}) with b " +
        s"CREATE (c:Client {id: ${client.id}, name:'${client.name}', age: ${client.age}})" +
        s" with b, c CREATE (b)-[:SEE]->(c) RETURN properties(c)")

    Future {
      session.close()
      Done
    }
  }

  def fetchClients(bankerId: Int): Future[Option[List[Client]]] = Future {
    val session = connection()
    val data: StatementResult =
      session.run(s"MATCH (b:Banker)-[:SEE]->(c) WHERE b.id = ${bankerId} RETURN properties(c)")
    var clientLists = List[Client]()
    while (data.hasNext) {
      val node = data.next
      node.fields
        .iterator()
        .asScala
        .foreach((t: Pair[String, Value]) => {
          clientLists ::= Client(bankerId,
                                 t.value().get("id").asInt(),
                                 t.value().get("name").asString(),
                                 t.value().get("age").asInt())
        })
    }
    session.close()
    Option(clientLists)
  }

  // all my routes
  val routes = {
    path("client") {
      post {
        entity(as[Client]) { client =>
          val saved: Future[Done] = addClient(client)
          onComplete(saved) { _ =>
            complete("client saved")
          }
        }
      }
    } ~
      get {
        path("banker" / IntNumber) { id =>
          val maybeItem: Future[Option[List[Client]]] = fetchClients(id)
          onSuccess(maybeItem) {
            case Some(clients) => complete(ToResponseMarshallable(clients))
            case None          => complete(StatusCodes.NotFound)
          }
        }
      }
  }

  val bindingFuture = Http().bindAndHandle(routes, "localhost", config.getInt("server.port"))

  println("Server online at localhost:8888 Press Enter to exit.")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
