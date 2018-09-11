package org.espercatz.record

case class AssociationRule(
  antecedent: Set[Int],
  consequent: Set[Int],
  confidence: Double,
  support: Int
)
