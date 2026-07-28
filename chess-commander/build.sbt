name := "chess-commander"

version := "0.1"

scalaVersion := "2.11.7"

lazy val versions = new {
  val finatra = "2.1.2"
  val logback = "1.1.3"
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com"
)

libraryDependencies += "com.twitter.finatra" % "finatra-http_2.11" % versions.finatra
libraryDependencies += "com.twitter.finatra" % "finatra-slf4j_2.11" % versions.finatra
libraryDependencies += "ch.qos.logback" % "logback-classic" % versions.logback