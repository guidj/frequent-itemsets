package org.mng

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


case class ItemSet(items: Set[Int], size: Int, support: Int)

object FrequentItemSet {

  def main(args: Array[String]): Unit = {

    val filePath = "/Users/guilherme/code/kth/id2222/frequent-itemset/src/main/resources/T10I4D100K.dat"
    val threshold = 3
//    val filePath = args(0)
//    val threshold = args(1)

    val cores = Runtime.getRuntime.availableProcessors

    val spark = SparkSession
      .builder()
      .master(s"local[$cores]")
      .appName("Frequent Itemset")
      .getOrCreate()

    import spark.implicits._

    val textFile = spark.read.textFile(filePath)

    val baskets = textFile.rdd.map(x => x.split(" ").map(_.toInt)).map(x => Set(x:_*))

    baskets.persist(StorageLevel.MEMORY_AND_DISK)

    val items = baskets.flatMap(_.map(e => (e, 1)))
      .reduceByKey((a, b) => a + b)
      .filter(x => x._2 >= threshold)

//    println(items.count)

    Apriori.transform(baskets, items, 3)
  }
}
