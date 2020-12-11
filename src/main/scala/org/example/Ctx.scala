package org.example

import akka.NotUsed
import akka.stream.scaladsl.Source
import org.example.dto.{EnrichmentService, Person}

import scala.concurrent.duration.DurationInt

case class Ctx(enrichmentService: EnrichmentService) {
  val peopleSource: Source[Person, NotUsed] = Source(1 to TOTAL_NUMBER_OF_PEOPLE_IN_THE_STREAM).map(id => Person(id, ""))
  val peopleSourceWindowed: Source[Seq[Person], NotUsed] = peopleSource.groupedWithin(WINDOW_BY_PERSON_AMOUNT, 1 second)
}
