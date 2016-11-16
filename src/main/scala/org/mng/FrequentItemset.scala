package org.mng

import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}


object FrequentItemSet {

  def main(args: Array[String]): Unit = {

    val filePath = args(0)

    val conf = new SparkConf().setAppName("Frequent Itemset")
    val sc = new SparkContext(conf)

    val textFile = sc.textFile(filePath)

//    val basketsRDD = textFile.map(_.split(" ").map(_.toInt)).map(list => collection.SortedSet(list: _*))
    val basketsRDD = textFile.map(_.split(" ").map(_.toInt))
    basketsRDD.persist(StorageLevel.MEMORY_AND_DISK)

    basketsRDD.take(5).foreach(_.foreach(x => print(x, " ")))

  }

}
