package org.espercatz

import scala.collection.mutable

import org.espercatz.domain.AssociationRule

object AssociationRules {

  def getAssociationRules(frequentItems: collection.mutable.Map[Set[Int], Int],
    confidenceThreshold: Double, supportThreshold: Int): mutable.MutableList[AssociationRule] = {
    var associationRules = mutable.MutableList[AssociationRule]()

    for ((itemSet, support) <- frequentItems) {
      val subsets = itemSet.subsets.filterNot(x => x.isEmpty || x.size == itemSet.size).toList
      val pairs = subsets.zip(subsets.reverse)

      for ((antecedent, consequent) <- pairs) {
        //TODO: Union can be repleced by just looking up the itemSet in the Map. But just to make
        // sure all pairs are right. I'm keeping it for now.
        val support = frequentItems(antecedent.union(consequent))
        val confidence = support / frequentItems(antecedent).toFloat
        if (support >= supportThreshold && confidence >= confidenceThreshold) {
          associationRules += AssociationRule(antecedent, consequent, confidence, support)
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
//          val pairs = subsets.flatMap(x => subsets.map(y => (x, y))).filterNot { case (a, b) =>
// a == b }
//        }
//      }
//    }
