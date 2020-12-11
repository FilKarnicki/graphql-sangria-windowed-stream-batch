package org.example

import akka.stream.Materializer
import akka.util.Timeout
import org.example.dto.{EnrichmentService, Person}
import sangria.execution.deferred.{Deferred, DeferredResolver}
import sangria.macros.derive.{AddFields, ReplaceField, deriveObjectType}
import sangria.schema.{Schema, _}
import sangria.streaming.akkaStreams._

import scala.concurrent.{ExecutionContext, Future}

object SchemaDefinition {
  def createSchema(implicit timeout: Timeout, ec: ExecutionContext, mat: Materializer): Schema[Ctx, Unit] = {
    val QueryType = ObjectType("Query", fields[Ctx, Unit](Field("notUsed", StringType, resolve = _ => "notUsed")))
    val PersonType = deriveObjectType[Unit, Person](
      ReplaceField("enrichmentString",
        Field("enrichmentString",
          StringType,
          resolve = ctx =>
            EnrichmentDeferred(ctx.value.id))))

    val SubscriptionType = ObjectType("Subscription", fields[Ctx, Unit](
      Field.subs("stream", PersonType, resolve = _.ctx.peopleSource.map(Action(_))),
      Field.subs("streamWindowed", ListType(PersonType), resolve = _.ctx.peopleSourceWindowed.map(Action(_)))
    ))

    Schema(QueryType, None, Some(SubscriptionType))
  }
}

class EnrichmentResolver(enrichmentService: EnrichmentService) extends DeferredResolver[Any] {
  def resolve(deferred: Vector[Deferred[Any]], ctx: Any, queryState: Any)(implicit ec: ExecutionContext) = {
    val personIds = deferred.map {
      case EnrichmentDeferred(id) => id
    }
    val enrichment = enrichmentService.enrichForIds(personIds)

    personIds.map(id =>
      Future.successful(enrichment.getOrElse(id, "COULD_NOT_ENRICH"))
    )
  }
}

case class EnrichmentDeferred(personId: Int) extends Deferred[String]
