name := "play-lab"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.9" exclude("org.scala-stm", "scala-stm_2.10.0")
)

play.Project.playScalaSettings
