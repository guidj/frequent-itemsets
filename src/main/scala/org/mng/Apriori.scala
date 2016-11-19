package org.mng

object Apriori {

  def transform(baskets: Map[Int, Set[Int]], items: Map[Int, Int], supportThreshold: Int, maxSetSize: Int = 3): Unit = {

    val reverseBasketTable = collection.mutable.Map[Int, collection.mutable.Set[Int]]()

    println("Starting with %d baskets, %d items.".format(baskets.size, items.size))

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

    val frequencyMap = collection.mutable.Map[Int, collection.mutable.Map[Set[Int], Int]]()

    val itemSet = items.filter(x => x._2 > supportThreshold).map(x => (Set(x._1), x._2))

    println("First filter leaves %d items that meet support threshold %d".format(itemSet.size, supportThreshold))
    var nSizedItemSet = collection.mutable.Map() ++ itemSet

    frequencyMap(1) = collection.mutable.Map() ++ itemSet

    for (i <- 1 until maxSetSize){
      val candidateItemSets = buildItemSets(baskets, nSizedItemSet.keys, items)
      val nPlusOneSizedItemSet = countItemSets(candidateItemSets, reverseBasketTable, supportThreshold)
      frequencyMap(i) = nPlusOneSizedItemSet
      nSizedItemSet = nPlusOneSizedItemSet

      val cnn = nPlusOneSizedItemSet.size

      println(s"Iteration $i found [$cnn] new frequent sets")
      nPlusOneSizedItemSet.take(5).foreach(x => println(s"\t$x"))
    }
  }

  def buildItemSets(baskets: Map[Int, Set[Int]], itemSet: Iterable[Set[Int]], frequentItems: Map[Int, Int]): List[Set[Int]] = {

    val combinations = for { x <- itemSet
      y <- baskets.map{ case (_, b) => b}
      if x forall (y contains)

    } yield (y -- x).filter(i => frequentItems.contains(i)).map(i => { x.union(Set(i))})
    combinations.flatten.toList.distinct
  }

  def findItemSetBaskets(itemSet: Set[Int], reverseBasketTable: collection.mutable.Map[Int, collection.mutable.Set[Int]]): Set[Int] ={
    val baskets = itemSet.map(i => reverseBasketTable(i)).reduce((a, b) => a.intersect(b))
    baskets.toSet
  }

  def countItemSets(itemSets: List[Set[Int]], reverseBasketTable: collection.mutable.Map[Int, collection.mutable.Set[Int]], supportThreshold: Int): collection.mutable.Map[Set[Int], Int] = {

    val itemCount = collection.mutable.Map[Set[Int], Int]()

    itemSets.foreach(items => {
        val basketsWithItemSet = findItemSetBaskets(items, reverseBasketTable)
        val count = basketsWithItemSet.size
      if (count >= supportThreshold)
        itemCount(items) = count
      })

    itemCount
  }
}
