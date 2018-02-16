package com.tw

import com.holdenkarau.spark.testing.DatasetSuiteBase
import org.scalatest.FunSuite
import org.scalatest.exceptions.TestFailedException

class DataSetTest extends FunSuite with DatasetSuiteBase{

  test("two Data Frame should be equal") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    val input1 = sc.parallelize(List(1,2,3)).toDF()
    assertDatasetEquals(input1, input1)
  }

  test("two Data Frame should not be equal") {
    val sqlCtx = sqlContext
    import sqlCtx.implicits._

    val input1 = sc.parallelize(List(1,2,3)).toDF()
    val input2 = sc.parallelize(List(2,3,4)).toDF()
    intercept[TestFailedException]{
      assertDatasetEquals(input1, input2)
    }
  }
}
