# Finding Frequent Itemsets

##Build and Run

##Apriori


## Association Rules
The part of the code creating association rules takes a Map of frequent Sets and their supoort as well as a
support- and a confidence-threshold as input. For every set it then creates pairs between all subsets of the set
(excluding the set and the empty set itself) whose union would create the set. Those are the association rules
that would lead to that set.it p For each of the association rules we then calculate their support and confidence.