package org.mng

object Apriori {

  def transform(baskets: Map[Int, Set[Int]], items: Map[Int, Int], supportThreshold: Int, limit: Int = 10): Unit = {

    val reverseBasketTable = collection.mutable.Map[Int, collection.mutable.Set[Int]]()

    println("Building reverse basket look up table")

    baskets.foreach {
      case (index, items) => {
        items.foreach(itemId => {
          val set = reverseBasketTable.getOrElse(itemId, collection.mutable.Set[Int]())
          set.add(index)
          reverseBasketTable(itemId) = set
        })
      }
    }

    val frequencyMap = collection.mutable.Map[Int, Map[Set[Int], Int]]()

    val itemSet = items.filter(x => x._2 > supportThreshold).map(x => (Set(x._1), x._2))
    frequencyMap(1) = itemSet

    val currentItemSet = itemSet

    val xyz = buildItemSets(itemSet, currentItemSet)

    println("Counting built sets")
    val tty = countItemSets(xyz, reverseBasketTable, supportThreshold)
    tty.take(10).foreach(println)
  }

  def buildItemSets(singleItemSet: Map[Set[Int], Int], itemSet: Map[Set[Int], Int]): List[Set[Int]] = {

    println(singleItemSet.keys)
    println(itemSet.keys)
    val combinations = for {x <- singleItemSet.keys
                            y <- itemSet.keys
                            if x != y
                            if x.intersect(y).isEmpty}
      yield x.union(y)

    combinations.toList.distinct
  }

  def countItemSets(itemSet: List[Set[Int]], reverseBasketTable: collection.mutable.Map[Int, collection.mutable.Set[Int]], supportThreshold: Int): Map[Set[Int], Int] = {
    itemSet.map(items => {
        val lookUpBasketsIndices = items.map(i => reverseBasketTable(i)).toList
        var basketsWithItemSet = lookUpBasketsIndices(0)
        for(i <- 1 until lookUpBasketsIndices.size){
          basketsWithItemSet = basketsWithItemSet.intersect(lookUpBasketsIndices(i))
        }
        val count = basketsWithItemSet.size
      (items, count)
      }).filter { case (_, c) => c >= supportThreshold }
      .toMap
  }
}
