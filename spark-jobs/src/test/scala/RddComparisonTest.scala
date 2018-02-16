import com.holdenkarau.spark.testing.{RDDComparisons, SharedSparkContext}
import org.scalatest.FunSuite

class RddComparisonTest
    extends FunSuite
    with SharedSparkContext
    with RDDComparisons {

  test("rdd comparison without order") {
    val expectedRDD = sc.parallelize(Seq(1, 2, 3))
    val resultRDD = sc.parallelize(Seq(3, 2, 1))

    assertRDDEquals(expectedRDD, resultRDD)
  }

  test("rdd comparison with order") {
    val expectedRDD = sc.parallelize(Seq(1, 2, 3))
    val resultRDD = sc.parallelize(Seq(1, 2, 3)) // if order of this sequence is not matched test will fail

    assertRDDEqualsWithOrder(expectedRDD, resultRDD)
  }

}
