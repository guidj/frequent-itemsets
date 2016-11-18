package org.mng

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

case class ItemSet(items: List[Int])

object FrequentItemSet {

  def frequentItemSets(basket: ItemSet, frequentItemSets: ItemSet): ItemSet = {
    ItemSet(List(1, 2))
  }

  def findFrequentItemSet(baskets: RDD[ItemSet], setSize: Int, frequentItemSet: Option[RDD[ItemSet]]): RDD[ItemSet] ={
    frequentItemSet.get
  }

  def main(args: Array[String]): Unit = {

    val filePath = "/home/maverik/IO/frequent-itemsets/src/main/resources/T10I4D100K.dat"//args(0)
    val cores = Runtime.getRuntime.availableProcessors

    val spark = SparkSession
      .builder()
      .master(s"local[$cores]")
      .appName("Frequent Itemset")
      .getOrCreate()

    val textFile = spark.read.textFile(filePath)

//    val basketsRDD = textFile.map(_.split(" ").map(_.toInt)).map(list => collection.SortedSet(list: _*))
    val basketsRDD = textFile.map(_.split(" ").toList.map(_.toInt))
    basketsRDD.persist(StorageLevel.MEMORY_AND_DISK)

    basketsRDD.take(5).foreach(
      values => {
        values.foreach(x => print(x, " "))
        print("\n")
      }
    )
  }

}
