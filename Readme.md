# Finding Frequent Itemsets

## Build and Run

The application takes as input 4 parameters:

  - *filePath*: input file, where each line is a basket, with space separated items
  - *support*: minimum number of times a set should be present to be considered valid
  - *confidence*: confidence level for association rules
  - *maxSetSize*: maximum size of sets to compute

To run the program, first build the jar:

```
~$ sbt assembly 
```

To run it, type:

```
$ java -jar target/scala-2.11/frequent-itemset-assembly-1.0.jar --input=src/main/resources/T10I4D100K.dat --min-support=1000 --min-confidence=.5 --max-set-size=3
```

The last 4 are parameters, path, support, confidence, and maximum set size

To see run options, type:

```
$ java -jar target/scala-2.11/frequent-itemset-assembly-1.0.jar --help
```

## Apriori

We compute the frequent item sets, using the Apriori algorithm.
For the first step, we determine the frequent items in the set, i.e. those that meet the support size, *s*. With the items, one should build the candidate pairs, and determine the frequent ones again. The process is similar moving forward at each stage.

To speed up processing, we took some steps to make it easier to look up data. We build a reverse look up table, where each item ID maps to a set of baskets where it belongs to. This is done for the frequent items only. 
Then, when building sets of size k+1, we look up the baskets of each item n in the set sized k, and compute the intersection between them. 
This intersection gives us the both (a) the baskets from which we can take candidate items to build the sets of size k+1, and (b) the support of the set n of size k, which is the number of baskets where all items are found.


## Association Rules
The part of the code creating association rules takes a Map of frequent Sets and their support as well as a
support- and a confidence-threshold as input. For every set m it then creates pairs between all subsets of the set
(excluding the set m itself and the empty set) whose union would create the set m. Those are the association rules
that would lead to that set. For each of the association rules we then calculate their support and confidence.
