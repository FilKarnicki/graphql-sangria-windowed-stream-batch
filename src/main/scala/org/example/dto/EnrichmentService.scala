package org.example.dto

class EnrichmentService {
  var numOfTimesCalled = 0;

  def enrichForIds(ids: Vector[Int]): Map[Int, String] = {
    numOfTimesCalled = numOfTimesCalled + 1
    ids.map(id =>
      (id, s"EnrichmentFor$id"))
      .toMap
  }
}
