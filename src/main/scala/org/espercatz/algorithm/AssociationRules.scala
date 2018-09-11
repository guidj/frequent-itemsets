package org.espercatz.algorithm

import org.espercatz.record.AssociationRule

/**
  * Created by guilherme on 9/10/18.
  * Default (Template) Project
  *
  */
object AssociationRules {

  def transform(frequentItems: Map[Set[Int], Int],
                confidenceThreshold: Double,
                supportThreshold: Int):
  Iterable[AssociationRule] = {
    frequentItems.flatMap {
      case (itemSet, _) =>
        val subsets = itemSet.subsets.filterNot(x => x.isEmpty || x.size == itemSet.size).toList
        val pairs = subsets.zip(subsets.reverse)

        pairs.flatMap {
          case (a, b) =>
            val support = frequentItems(a.union(b))
            val confidence = support / frequentItems(a).toFloat
            if (support >= supportThreshold && confidence >= confidenceThreshold) {
              Some(AssociationRule(a, b, confidence, support))
            } else {
              None
            }
        }
    }
  }
}
