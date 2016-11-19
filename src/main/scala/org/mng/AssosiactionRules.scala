package org.mng

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.dmg.pmml.Itemset

import scala.collection.mutable

case class AssociationRule(rule: (Set[Int],Set[Int]), confidence: Double, support: Int)

class AssociationRules {

  val spark = SparkSession.builder().master("local").getOrCreate()

  def getAssociationRules(frequentItems: Map[Set[Int], Int], confidenceThreshold: Double, supportThreshold: Int): mutable.MutableList[AssociationRule] = {
    var associationRules = mutable.MutableList[AssociationRule]()

    for ((itemSet, support) <- frequentItems) {
      val subsets = itemSet.subsets.filterNot(x => x.isEmpty || x.size == itemSet.size).toList
      val pairs = subsets.zip(subsets.reverse)

      for ((set1, set2) <- pairs) {
        //TODO: Union can be repleced by just looking up the itemSet in the Map. But just to make sure all pairs are right. I'm keeping it for now.
        val support = frequentItems(set1.union(set2))
        val confidence = support / frequentItems(set1)
        if(support >= supportThreshold && confidence >= confidenceThreshold){
          associationRules += AssociationRule((set1,set2), confidence, support)
        }

      }

    }
    associationRules
  }
}


//    var i = frequentItems.select("Max(size)").collect()(0).getInt(0)
//
//    while(i > 1){
//      println(s"calculating association rules of size $i")
//      frequentItems.select("itemSet").where(s"size == $i").foreach{
//        itemSet => {
//          val subsets = itemSet.subsets.filterNot(x => x.isEmpty || x.size == itemSet.size).toList
//          val pairs = subsets.flatMap(x => subsets.map(y => (x, y))).filterNot { case (a, b) => a == b }
//        }
//      }
//    }
