package org.espercatz

import collection.mutable
import scala.collection.parallel.ParSet

import org.espercatz.domain.AssociationRule

object Apriori {

  def transform(baskets: Map[Int, Set[Int]], frequentItems: Map[Int, Int], supportThreshold: Int, confidenceThreshold: Double, maxSetSize: Int = 3): mutable.MutableList[AssociationRule] = {

    val reverseBasketHT = mutable.Map[Int, mutable.Set[Int]]()

    println(s"Starting with ${baskets.size} baskets, ${frequentItems.size} items.")

    val frequentItemSets = mutable.Map[Set[Int], Int]()

    frequentItems.foreach(
      x => {
        frequentItemSets(Set(x._1)) = x._2
      }
    )

    println("Building reverse basket look up table")

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

    while (iteration <= maxSetSize) {
      val candidateItemSets = buildItemSets(nSizedItemSet.keys.toList, frequentItemSets, baskets, reverseBasketHT, frequentItems)
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

  def findItemSetBaskets(itemSet: Set[Int], reverseBasketHT: mutable.Map[Int, mutable.Set[Int]]): Set[Int] = {
    val baskets = itemSet.map(i => reverseBasketHT(i)).reduce((a, b) => a.intersect(b))
    baskets.toSet
  }

  //TODO: do we always need to check frequency?
  //Should be go from bucket to pairs, or pairs to buckets

  def buildItemSets(itemSets: List[Set[Int]], frequentItemSets: mutable.Map[Set[Int], Int], baskets: Map[Int, Set[Int]], reverseBasketHT: mutable.Map[Int, mutable.Set[Int]], frequentItems: Map[Int, Int]): ParSet[Set[Int]] = {

    //    create pairs of size n, where all subsets of size n-1 are frequent

    val newSets = itemSets.par.zipWithIndex.flatMap{ case (itemSet, i) => {
      val candidates = itemSets.zipWithIndex.filter{ case(_, j) => j > i}
        .flatMap{ case(secondItemSet, _) => itemSet.union(secondItemSet).subsets(itemSet.size + 1)}
          .filter(candidateSet => {
            val subsets = candidateSet.subsets(itemSet.size)
            var c = 0
            var total = 0
            for(subset <- subsets){
              if (frequentItemSets.contains(subset)) {
                c += 1
              }

              total += 1
            }
            c == total
          })
      candidates
    }}.toSet

    newSets
  }

  //TODO: do we always need to check frequency?
  //Should be go from bucket to pairs, or pairs to buckets

  def filterItemSets(candidateItemSets: ParSet[Set[Int]], frequentItemSets: mutable.Map[Set[Int], Int], reverseBasketHT: mutable.Map[Int, mutable.Set[Int]], supportThreshold: Int): mutable.Map[Set[Int], Int] = {

    val itemCount = mutable.Map[Set[Int], Int]()

    candidateItemSets.par.foreach(items => {
      val basketsWithItemSet = findItemSetBaskets(items, reverseBasketHT)
      val count = basketsWithItemSet.size
      if (count >= supportThreshold) {
        itemCount(items) = count
        frequentItemSets(items) = count
      }
    })

    itemCount
  }
}
