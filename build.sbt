name := "graphql-sangria-windowed-stream-batch"
version := "0.1.0-SNAPSHOT"

description := "An attempt to window subscriptions"

scalaVersion := "2.12.2"
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria" % "1.2.0",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.0",
  "org.sangria-graphql" %% "sangria-akka-streams" % "1.0.0",

  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.6",
  "de.heikoseeberger" %% "akka-sse" % "2.0.0",

  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalatest" %% "scalatest-flatspec" % "3.2.2" % "test",

  // akka-http still depends on 2.4 but should work with 2.5 without problems
  // see https://github.com/akka/akka-http/issues/821
  "com.typesafe.akka" %% "akka-stream" % "2.5.1",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.1" % "test"
)

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
