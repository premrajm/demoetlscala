package com.tw.data.service

import java.util.function.Consumer

import akka.Done
import akka.actor.{ActorLogging, ActorSystem}
import akka.http.javadsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.ContentNegotiator.Alternative.ContentType
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.neo4j.driver.v1._
import org.neo4j.driver.v1.util.Pair
import play.api.libs.json.{JsArray, JsPath, Json}
import play.api.libs.json.__

import scala.concurrent.Future
import scala.io.StdIn

object BankerService extends App with PlayJsonSupport {

  val config = ConfigFactory.load().getConfig("neo4j")

  implicit val system = ActorSystem("my-actors")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  final case class Client(bankerId: Int,id: Int, name: String, age: Int){
    require(bankerId != null || bankerId <= 0, "Banker Id is mandatory")
  }

  final object Client {
    implicit val clientFormat = Json.format[Client]
    implicit val responseFormat = Json.format[GetClientsResponse]
  }

  final case class GetClientsResponse(clients: List[Client])


  // TODO: fetch this out my ide is giving error for Neo4jConnection class on compile
  def getConnection() = {
    val driver = GraphDatabase.driver(config.getString("url"), AuthTokens.basic(config.getString("username"),
      config.getString("password")))
    driver.session()
  }


  def addClient(client: Client): Future[Done] = {
    val session:Session = getConnection

    // Todo: can this be better handled using OGM ?
    val data = session.run(s"MATCH (b:Banker {id: ${client.bankerId}}) with b " +
      s"CREATE (c:Client {id: ${client.id}, name:'${client.name}', age: ${client.age}})" +
      s" with b, c CREATE (b)-[:SEE]->(c) RETURN c")

    println(s"insert count:" + data.single().get(0).asString())
    Future {
      Done
    }
  }

  def fetchClients(bankerId: Int): Future[Option[List[Client]]] = Future {
    val session = getConnection()
    val data:StatementResult = session.run(s"MATCH (b:Banker)-[:SEE]->(c) WHERE b.id = ${bankerId} RETURN properties(c)")
    var clientLists = List[Client]()
    while(data.hasNext){
      val node = data.next()
      node.fields().forEach(new Consumer[Pair[String, Value]] {
        override def accept(t: Pair[String, Value]): Unit = {
          clientLists ::= Client(bankerId, t.value().get("id").asInt(), t.value().get("name").asString(), t.value().get("age").asInt())
        }
      })

    }
    Option(clientLists)
  }

  // all my routes
  val routes = {
    path("client") {
      post {
        entity(as[Client]) { client =>
          val saved: Future[Done] = addClient(client)
          onComplete(saved) { _ => complete("client saved") }
        }
      }
    } ~
    get {
      path("banker" / IntNumber) { id =>
        val maybeItem: Future[Option[List[Client]]] = fetchClients(id)
        onSuccess(maybeItem) {
          case Some(clients) => complete(ToResponseMarshallable(clients))
          case None => complete(StatusCodes.NotFound)
        }
      }
    }
  }


  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8888)

  println("Server online at localhost:8888 >> Press Enter to exit.")
  StdIn.readLine()

  bindingFuture
        .flatMap(_.unbind())
          .onComplete(_ => system.terminate())

}


