package org.mng


case class ItemSet(items: Set[Int], size: Int, support: Int)

object FrequentItemSet {

  def main(args: Array[String]): Unit = {

    //TODO: stream baskets from file (on each iteration, save memory)

//    val filePath = "src/main/resources/T10I4D100K.dat"
//    val supportThreshold = 3
    val filePath = args(0)
    val supportThreshold = args(1).toInt

    val textFile = scala.io.Source.fromFile(filePath).getLines()

    val baskets = textFile.map(x => x.split(" ").map(_.toInt))
      .zipWithIndex
      .map{case (x, index) => index -> Set(x:_*)}
      .toList

    val items = baskets.flatMap{ case (_, items) => items.map(e => (e, 1))}

    val itemsFrequency = collection.mutable.Map[Int, Int]()

    items.foreach {
      item => {
        itemsFrequency(item._1) = item._2 + itemsFrequency.getOrElse(item._1, 0)
      }
    }

    val frequentItemSet = itemsFrequency.filter{ case (a, b) => b >= supportThreshold }

    Apriori.transform(baskets.toMap, frequentItemSet.toMap, supportThreshold)
  }
}
