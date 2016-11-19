package org.mng

import org.apache.spark.rdd.RDD

object Apriori {

  //TODO: can a dataframe do better?

  def transform(baskets: RDD[Set[Int]], items: RDD[(Int, Int)], threshold: Int, limit: Int = 10): Unit ={

    val itemSet = items.map(x => (Set(x._1), x._2))

    val currentItemSet = itemSet

    val xyz = buildItemSets(itemSet, currentItemSet, threshold)
    println(xyz.count)
//    xyz.take(10).foreach(println)

    val tty = countItemSets(xyz, baskets, threshold)

    tty.take(10).foreach(println)
//    for(i <- 1 to limit){
//      currentItemSet = buildItemSets(itemSet, currentItemSet, threshold)
//      println(currentItemSet.count)
//
//    }
  }

  def buildItemSets(singleItemSet: RDD[(Set[Int], Int)], itemSet: RDD[(Set[Int], Int)], threshold: Int): RDD[Set[Int]] ={
    val combinations = singleItemSet.map(x => x._1)
      .cartesian(itemSet.map(x => x._1))
      .filter{ case (a, b) => a != b }
      .map{ case (a, b) => a.union(b) }
      .distinct

    combinations
  }

  def countItemSets(itemSet: RDD[Set[Int]], baskets: RDD[Set[Int]], threshold: Int): RDD[(Set[Int], Int)] ={
    itemSet
      .map{ items => {
      val count = baskets.map(basketItems => if (basketItems.intersect(items).nonEmpty) 1 else 0).reduce(_+_)
        (items, count)
    }}
      .filter{ case (_, count) => count >= threshold }
  }
}
