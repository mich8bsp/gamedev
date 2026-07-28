name := "khet-game-server"

version := "0.1"

scalaVersion := "2.12.10"

lazy val versions = new {
  val finatra = "20.5.0"
  val logback = "1.1.3"
}

enablePlugins(JavaAppPackaging)


resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com"
)

libraryDependencies += "com.twitter" % "finatra-http_2.12" % versions.finatra
libraryDependencies += "com.twitter" % "finatra-slf4j_2.12" % "2.13.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % versions.logback
libraryDependencies +=  "com.typesafe.akka" %% "akka-actor" % "2.6.6"