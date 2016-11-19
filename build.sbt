name := "frequent-itemset"

version := "1.0"

scalaVersion := "2.11.8"

resolvers ++= Seq("maven.org" at "http://repo2.maven.org/maven2",
  "conjars.org" at "http://conjars.org/repo")

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.0.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.0.1"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.0.1"

//libraryDependencies += "org.apache.spark" %% "spark-core" % "2.0.1" % "provided"
//libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.0.1" % "provided"
//libraryDependencies += "org.apache.spark" %% "spark-mllib" % "2.0.1" % "provided"
//libraryDependencies += "org.scala-lang" % "scala-library" % "2.11.8"
libraryDependencies += "org.log4s" %% "log4s" % "1.3.3" % "provided"

mainClass in Compile := Some("org.mng.FrequentItemSet")

//assemblyMergeStrategy in assembly := {
//  case n if n startsWith "org/apache/commons" => MergeStrategy.first
//  case n if n startsWith "org/apache/spark" => MergeStrategy.first
//  case n if n startsWith "org/apache/hadoop" => MergeStrategy.first
//  case n if n startsWith "org/aopalliance" => MergeStrategy.first
//  case n if n startsWith "javax/inject" => MergeStrategy.first
//  case n => val oldStrategy = (assemblyMergeStrategy in assembly).value
//    oldStrategy(n)
//}

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)