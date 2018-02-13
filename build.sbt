lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.fancy_exchange",
      scalaVersion := "2.12.4",
      version      := "0.0.1"
    )),
    name := "Fancy Exchange",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test
  )
