name := "frequent-itemset"

version := "1.0"

scalaVersion := "2.11.8"

resolvers ++= Seq("maven.org" at "http://repo2.maven.org/maven2",
  "conjars.org" at "http://conjars.org/repo")

libraryDependencies += "org.log4s" %% "log4s" % "1.3.3" % "provided"

mainClass in Compile := Some("org.mng.FrequentItemSet")

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true)