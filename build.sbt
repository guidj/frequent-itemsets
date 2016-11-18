name := "frequent-itemset"

version := "1.0"

scalaVersion := "2.11.8"

resolvers ++= Seq("maven.org" at "http://repo2.maven.org/maven2",
  "conjars.org" at "http://conjars.org/repo")

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.0.2"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.0.2"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.0.2"

//libraryDependencies += "org.apache.spark" %% "spark-core" % "2.0.2" % "provided"
//libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.0.2" % "provided"
//libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.0.2" % "provided"
//libraryDependencies += "org.scala-lang" % "scala-library" % "2.11.8"

mainClass in Compile := Some("org.mng.Main")

assemblyMergeStrategy in assembly := {
  case n if n startsWith "com/esotericsoftware" => MergeStrategy.first
  case n => val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(n)
}