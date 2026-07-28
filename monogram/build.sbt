name := "monogram"

version := "0.1"

scalaVersion := "2.12.7"

enablePlugins(ScalaJSPlugin)

scalaJSUseMainModuleInitializer := true
mainClass in Compile := Some("com.github.monogram.PageBuilder")

libraryDependencies += "de.sciss" %% "scalamidi" % "0.2.1"
libraryDependencies += "com.twitter" %% "finatra-http" % "18.9.1"
