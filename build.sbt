name := "GDA Corpus Browser"

version := "1.0.0"

lazy val root = (project in file("."))

scalaVersion := "2.11.8"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-Ywarn-unused-import")

scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import"))

mainClass in (Compile, run) := Some("jp.or.gsk.gdacb.GDA_Corpus_Browser")

fork in run := true
