package org.espercatz

import java.util.concurrent.TimeUnit

import org.espercatz.task.FrequentItemSetTask

/**
  * Created by guilherme on 9/10/18.
  * frequent-itemsets
  *
  */
object App {

  def main(args: Array[String]): Unit = {

    val params = Array(
      "--input=src/main/resources/T10I4D100K.dat",
      "--min-support=500",
      "--min-confidence=.5",
      "--max-itemsSet-size=5"
    )

    def run(c: Int): List[Long] = {
      def go(left: Int, executionTimes: List[Long]): List[Long] = {
        if (left <= 0){
          executionTimes
        } else {
          val start = System.nanoTime()
          FrequentItemSetTask.main(params)
          val end = System.nanoTime()
          val duration = end - start

          go(left - 1, duration :: executionTimes)
        }
      }

      go(c, Nil)
    }


    val executionTimes = run(5)
    executionTimes.reverse.zipWithIndex.foreach{
      case (execTime, index) =>
        println(s"Run ${index + 1} took ${TimeUnit.SECONDS.convert(execTime, TimeUnit.NANOSECONDS)}s")
    }

    val averageExecutionTime = executionTimes.sum/executionTimes.size
    println(s"Average runtime over ${executionTimes.size} runs: " +
      s"${TimeUnit.SECONDS.convert(averageExecutionTime, TimeUnit.NANOSECONDS)}s")
  }
}
