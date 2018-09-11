package org.espercatz.task

import org.espercatz.algorithm.Apriori
import org.rogach.scallop.ScallopConf

/**
  * Created by guilherme on 9/10/18.
  * Default (Template) Project
  *
  */
object FrequentItemSetTask {

  case class Args(arguments: Seq[String]) extends ScallopConf(arguments) {
    val input = opt[String](
      "input",
      required = true,
      descr = "Path to file with baskets. Each line is a space separated list of items"
    )
    val minSupport = opt[Int](
      "min-support",
      required = true,
      descr = "Min support for association rules",
      validate = _ > 0
    )
    val minConfidence = opt[Double](
      "min-confidence",
      required = true,
      descr = "Min confidence for association rules",
      validate = v => v > 0.0 && v <= 1.0
    )
    val maxSetSize = opt[Int](
      "max-set-size",
      required = true,
      descr = "Max size of candidate sets",
      validate = _ > 1
    )
    verify()
  }

  def usage(): Unit = {
    val message =
      """
        |FrequentItemSetTask [filePath] [supportThreshold] [confidenceThreshold] [maxSetSize]
      """.stripMargin
    println(message)
  }

  def main(cmdArgs: Array[String]): Unit = {

    val args = Args(cmdArgs)

    val textFile = scala.io.Source.fromFile(args.input()).getLines()

    val baskets = textFile.map(x => x.split(" ").map(_.toInt))
      .zipWithIndex
      .map { case (x, index) => index -> Set(x: _*) }
      .toMap

    val associationRules = Apriori.transform(
      baskets,
      args.minSupport(),
      args.minConfidence(),
      args.maxSetSize()
    )

    for (rule <- associationRules) {
      println(f"${rule.antecedent}%-25s => ${rule.consequent}%-25s" +
        s"\t[c: ${rule.confidence}, s: ${rule.support}]")
    }
  }
}
