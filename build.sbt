name := "frequent-itemset"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.play" % "play-json_2.11" % "2.5.4"
libraryDependencies += "org.apache.hadoop" % "hadoop-core" % "1.2.1" % "provided"
libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.6.4" % "provided"
libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "1.6.2" % "provided"
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "1.6.2" % "provided"
libraryDependencies += "org.scala-lang" % "scala-library" % "2.11.8"

resolvers ++= Seq("maven.org" at "http://repo2.maven.org/maven2",
  "conjars.org" at "http://conjars.org/repo")

mainClass in Compile := Some("org.mng.Main")

assemblyMergeStrategy in assembly := {
  case n if n startsWith "com/esotericsoftware" => MergeStrategy.first
  case n => val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(n)
}