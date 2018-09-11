name := "frequent-itemset"

version := "1.0"

scalaVersion := "2.11.8"

val scallopVersion = "3.1.3"

resolvers ++= Seq("maven.org" at "http://repo2.maven.org/maven2",
  "conjars.org" at "http://conjars.org/repo")

libraryDependencies ++= Seq(
  "org.log4s" %% "log4s" % "1.3.3" % "provided",
  "org.rogach" %% "scallop" % scallopVersion
)

mainClass in Compile := Some("org.espercatz.task.FrequentItemSetTask")

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true)