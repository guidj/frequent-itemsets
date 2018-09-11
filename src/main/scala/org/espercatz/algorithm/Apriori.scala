package org.espercatz.algorithm

import org.espercatz.record.AssociationRule

/**
  * Created by guilherme on 9/10/18.
  * Default (Template) Project
  *
  */
object Apriori {

  def transform(baskets: Map[Int, Set[Int]],
                supportThreshold: Int,
                confidenceThreshold: Double,
                maxSetSize: Int = 3): Iterable[AssociationRule] = {
    val frequentItemCount = baskets.toStream
      .flatMap { case (_, items) => items.map(e => (e, 1)) }
      .groupBy { case (itemId, _) => itemId }
      .map { case (itemId, iterable) => (itemId, iterable.size) }
      .filter { case (_, count) => count >= supportThreshold }

    println(s"Starting with ${baskets.size} baskets, ${frequentItemCount.size} frequent items.")

    println("Building reverse basket look up table")

    val reverseBasketHT = baskets.toStream.flatMap {
      case (basketId, items) =>
        items.map(i => (i, basketId))
    }.groupBy {
      case (i, _) => i
    }.map {
      case (i, bs) => (i, bs.map(_._2).toSet)
    }

    println(s"Reverse basket HT size: ${reverseBasketHT.size}")

    def go(nSizedSet: List[Set[Int]], frequentSetsCount: Map[Set[Int], Int], setSize: Int): Map[Set[Int], Int] = {
      if (setSize > maxSetSize) {
        frequentSetsCount
      } else {
        val candidateItemSets = candidates(
          nSizedSet, // prev gen
          setSize,
          frequentSetsCount, // prev gen agg
          baskets,
          reverseBasketHT,
          frequentItemCount
        )

        val nPlusOneSizedItemSet = filter(candidateItemSets, reverseBasketHT, supportThreshold)
        println(s"Iteration ${maxSetSize - setSize}  found [${nPlusOneSizedItemSet.size}] new frequent sets")

        go(nPlusOneSizedItemSet.keys.toList, nPlusOneSizedItemSet ++ frequentSetsCount, setSize + 1)
      }
    }

    val gen0FrequentSetsCount = frequentItemCount.map {
      case (i, c) => (Set(i), c)
    }
    val gen0FrequentSets = gen0FrequentSetsCount.keys.toList

    val frequentSetsCount = go(gen0FrequentSets, gen0FrequentSetsCount, 1)

    AssociationRules.transform(
      frequentSetsCount,
      confidenceThreshold,
      supportThreshold
    )
  }

  def candidates(itemSets: List[Set[Int]],
                 setSize: Int,
                 frequentItemSets: Map[Set[Int], Int],
                 baskets: Map[Int, Set[Int]],
                 reverseBasketHT: Map[Int, Set[Int]],
                 frequentItems: Map[Int, Int]):
  Set[Set[Int]] = {
    itemSets.combinations(2)
      .flatMap {
        case a :: b :: Nil => Some(a, b)
        case _ => None
      }.flatMap { case (setA, setB) => setA.union(setB).subsets(setSize + 1) }
      .filter {
        candidateSet =>
          val subsets = candidateSet.subsets(setSize).toList
          // TODO: this hashing is expensive. It computes setA's hash and throws it away. Fix it!
          subsets.count(frequentItemSets.contains) == (setSize + 1)
      }.toSet
  }

  def basketsInCommon(items: Set[Int],
                      reverseBasketHT: Map[Int, Set[Int]]): Set[Int] = {
    items.map(i => reverseBasketHT(i)).reduce((a, b) => a.intersect(b))
  }

  def filter(candidateItemSets: Set[Set[Int]],
             reverseBasketHT: Map[Int, Set[Int]],
             supportThreshold: Int):
  Map[Set[Int], Int] = {
    candidateItemSets.toStream.map {
      items => (items, basketsInCommon(items, reverseBasketHT).size)
    }.filter {
      case (_, basketSize) => basketSize >= supportThreshold
    }.toMap
  }
}
