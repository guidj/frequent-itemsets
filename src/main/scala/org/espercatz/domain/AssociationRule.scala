package org.espercatz.domain

case class AssociationRule(
  antecedent: Set[Int],
  consequent: Set[Int],
  confidence: Double,
  support: Int
)
