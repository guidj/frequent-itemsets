package org.mng

import collection.mutable

object Apriori {

  def transform(baskets: Map[Int, Set[Int]], items: Map[Int, Int], supportThreshold: Int, confidenceThreshold: Double, maxSetSize: Int = 3): mutable.MutableList[AssociationRule] = {

    val reverseBasketTable = mutable.Map[Int, mutable.Set[Int]]()

    println(s"Starting with ${baskets.size} baskets, ${items.size} items.")

    println("Building reverse basket look up table")

    baskets.foreach {
      case (index, items) => {
        items.foreach(itemId => {
          val set = reverseBasketTable.getOrElse(itemId, mutable.Set[Int]())
          set.add(index)
          reverseBasketTable(itemId) = set
        })
      }
    }

    val frequentItemSets = mutable.Map[Set[Int], Int]()

//    val itemSet = items.filter(x => x._2 > supportThreshold).map(x => (Set(x._1), x._2))
    items.filter(x => x._2 >= supportThreshold).foreach(
      x => {
        frequentItemSets(Set(x._1)) = x._2
      }
    )

    println(s"Frequent item sets of size 1: ${frequentItemSets.size}")

    var nSizedItemSet = mutable.Map[Set[Int], Int]() ++ frequentItemSets

    for (i <- 1 until maxSetSize){
      val candidateItemSets = buildItemSets(baskets, nSizedItemSet.keys, items)
      val nPlusOneSizedItemSet = filterItemSets(candidateItemSets, frequentItemSets, reverseBasketTable, supportThreshold)
      nSizedItemSet = nPlusOneSizedItemSet

      val cnn = nPlusOneSizedItemSet.size

      println(s"Iteration $i found [$cnn] new frequent sets")
      nPlusOneSizedItemSet.take(5).foreach(x => println(s"\t$x"))
    }

    val associationRules = AssociationRules.getAssociationRules(frequentItemSets, confidenceThreshold, supportThreshold)
    
    associationRules
  }

  def buildItemSets(baskets: Map[Int, Set[Int]], itemSet: Iterable[Set[Int]], frequentItems: Map[Int, Int]): List[Set[Int]] = {

    val combinations = for { x <- itemSet
      y <- baskets.map{ case (_, b) => b}
      if x forall (y contains)

    } yield (y -- x).filter(i => frequentItems.contains(i)).map(i => { x.union(Set(i)) })
    combinations.flatten.toList.distinct
  }

  def findItemSetBaskets(itemSet: Set[Int], reverseBasketTable: mutable.Map[Int, mutable.Set[Int]]): Set[Int] ={
    val baskets = itemSet.map(i => reverseBasketTable(i)).reduce((a, b) => a.intersect(b))
    baskets.toSet
  }

  def filterItemSets(candidateItemSets: List[Set[Int]], frequentItemSets: mutable.Map[Set[Int], Int], reverseBasketTable: mutable.Map[Int, mutable.Set[Int]], supportThreshold: Int): mutable.Map[Set[Int], Int] = {

    val itemCount = mutable.Map[Set[Int], Int]()

    candidateItemSets.foreach(items => {
        val basketsWithItemSet = findItemSetBaskets(items, reverseBasketTable)
        val count = basketsWithItemSet.size
      if (count >= supportThreshold)
        itemCount(items) = count
        frequentItemSets(items) = count
      })

    itemCount
  }
}
