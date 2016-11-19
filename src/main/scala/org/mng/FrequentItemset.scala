package org.mng


case class ItemSet(items: Set[Int], size: Int, support: Int)

object FrequentItemSet {

  def usage(): Unit ={
    val message =
      """
        |FrequentItemSet [filePath] [supportThreshold] [maxSetSize]
      """.stripMargin
    println(message)
  }

  def main(args: Array[String]): Unit = {

    if (args.length < 3){
      usage()
      System.exit(1)
    }

    val filePath = args(0)
    val supportThreshold = args(1).toInt
    val maxSetSize = args(2).toInt

    val textFile = scala.io.Source.fromFile(filePath).getLines()

    val baskets = textFile.map(x => x.split(" ").map(_.toInt))
      .zipWithIndex
      .map{case (x, index) => index -> Set(x:_*)}
      .toList

    val items = baskets.flatMap{ case (_, items) => items.map(e => (e, 1))}

    val frequentItemSet = items.groupBy{ case (id, c) => id }
      .map{ case (key, values) => (key, values.map(v => v._2).sum) }
      .filter{ case(key, count) => count >= supportThreshold }

    Apriori.transform(baskets.toMap, frequentItemSet, supportThreshold, maxSetSize)
  }
}
