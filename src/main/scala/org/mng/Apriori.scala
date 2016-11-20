package org.mng

import collection.mutable

object Apriori {

  def transform(baskets: Map[Int, Set[Int]], frequentItems: Map[Int, Int], supportThreshold: Int, confidenceThreshold: Double, maxSetSize: Int = 3): mutable.MutableList[AssociationRule] = {

    val reverseBasketHT = mutable.Map[Int, mutable.Set[Int]]()

    println(s"Starting with ${baskets.size} baskets, ${frequentItems.size} items.")

    println("Building reverse basket look up table")

    val frequentItemSets = mutable.Map[Set[Int], Int]()

    frequentItems.foreach(
      x => {
        frequentItemSets(Set(x._1)) = x._2
      }
    )

    baskets.foreach {
      case (index, items) => {
        items.foreach(itemId => {
          if (frequentItems.contains(itemId)) {
            val set = reverseBasketHT.getOrElse(itemId, mutable.Set[Int]())
            set.add(index)
            reverseBasketHT(itemId) = set
          }
        })
      }
    }

    println(s"Frequent item sets of size 1: ${frequentItemSets.size}")

    var nSizedItemSet = mutable.Map[Set[Int], Int]() ++ frequentItemSets
    var iteration = 1

    while(iteration <= maxSetSize){
      val candidateItemSets = buildItemSets(nSizedItemSet.keys, baskets, reverseBasketHT, frequentItems)
      val nPlusOneSizedItemSet = filterItemSets(candidateItemSets, frequentItemSets, reverseBasketHT, supportThreshold)
      nSizedItemSet = nPlusOneSizedItemSet

      val cnn = nPlusOneSizedItemSet.size

      println(s"Iteration $iteration found [$cnn] new frequent sets")
      nPlusOneSizedItemSet.take(5).foreach(x => println(s"\t$x"))
      iteration += 1
    }

    val associationRules = AssociationRules.getAssociationRules(frequentItemSets, confidenceThreshold, supportThreshold)
    
    associationRules
  }


  def findItemSetBaskets(itemSet: Set[Int], reverseBasketHT: mutable.Map[Int, mutable.Set[Int]]): Set[Int] ={
    val baskets = itemSet.map(i => reverseBasketHT(i)).reduce((a, b) => a.intersect(b))
    baskets.toSet
  }

  def buildItemSets(itemSets: Iterable[Set[Int]], baskets: Map[Int, Set[Int]], reverseBasketHT: mutable.Map[Int, mutable.Set[Int]], frequentItems: Map[Int, Int]): Set[Set[Int]] = {

    itemSets.flatMap(itemSet => {
      findItemSetBaskets(itemSet, reverseBasketHT)
        .map(basketId => baskets(basketId))
        .flatMap(basket => (basket -- itemSet).filter(i => frequentItems.contains(i)).map(i => itemSet.union(Set(i))))
    }).toSet
  }

  def filterItemSets(candidateItemSets: Set[Set[Int]], frequentItemSets: mutable.Map[Set[Int], Int], reverseBasketHT: mutable.Map[Int, mutable.Set[Int]], supportThreshold: Int): mutable.Map[Set[Int], Int] = {

    val itemCount = mutable.Map[Set[Int], Int]()

    candidateItemSets.foreach(items => {
        val basketsWithItemSet = findItemSetBaskets(items, reverseBasketHT)
        val count = basketsWithItemSet.size
      if (count >= supportThreshold)
        itemCount(items) = count
        frequentItemSets(items) = count
      })

    itemCount
  }
}
