package org.mng

import org.apache.spark.{SparkContext, SparkConf}


object Main {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("Frequent Itemset")
    val sc = new SparkContext(conf)

//    val textFile = sc.textFile(input)

  }

}
