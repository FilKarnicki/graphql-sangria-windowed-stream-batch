package org.example


import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.example.dto.EnrichmentService
import org.scalatest.Matchers.{be, convertToAnyShouldWrapper}
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import sangria.execution.Executor
import sangria.macros.LiteralGraphQLStringContext
import sangria.marshalling.sprayJson._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps

class SubscriptionTest extends AnyFlatSpec with OptionValues with Inside with Inspectors {

  import system.dispatcher

  implicit val system = ActorSystem("server")
  implicit val materializer = ActorMaterializer()
  implicit val timeout = Timeout(10 seconds)

  it should "not batch normally" in {
    val query =
      graphql"""
               subscription {
                      stream {
                        id
                        enrichmentString
                      }
               }"""
    import sangria.execution.ExecutionScheme.Stream
    import sangria.streaming.akkaStreams._

    val enrichmentService = new EnrichmentService

    val resultFuture =
      Executor
        .execute(
          SchemaDefinition.createSchema,
          query,
          Ctx(enrichmentService),
          deferredResolver = new EnrichmentResolver(enrichmentService))
        .runForeach(println(_))

    Await.ready(resultFuture, Duration.Inf)
    enrichmentService.numOfTimesCalled should be(TOTAL_NUMBER_OF_PEOPLE_IN_THE_STREAM)
  }

  it should "batch with windowing" in {
    val query =
      graphql"""
               subscription {
                      streamWindowed {
                        id
                        enrichmentString
                      }
               }"""
    import sangria.execution.ExecutionScheme.Stream
    import sangria.streaming.akkaStreams._

    val enrichmentService = new EnrichmentService

    val resultFuture =
      Executor
        .execute(SchemaDefinition.createSchema, query, Ctx(enrichmentService), deferredResolver = new EnrichmentResolver(enrichmentService))
        .runForeach(println(_))

    Await.ready(resultFuture, Duration.Inf)
    enrichmentService.numOfTimesCalled should be(TOTAL_NUMBER_OF_PEOPLE_IN_THE_STREAM / WINDOW_BY_PERSON_AMOUNT)
  }
}
