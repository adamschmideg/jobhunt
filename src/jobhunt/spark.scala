import org.apache.spark.sql.functions._

val toInt    = udf[Int, String]( _.toInt)
val toDouble = udf[Double, String]( _.toDouble)

val raw = spark.read.option("delimiter", ",").option("header", "true").csv("companies.csv")
val companies = raw
.withColumn("numberOfRatings", toDouble(raw("numberOfRatings")))
.withColumn("recommendToFriend", toDouble(raw("recommendToFriendRating")))
.withColumn("overallRating", toDouble(raw("overallRating")))
.withColumn("compensationAndBenefits", toDouble(raw("compensationAndBenefitsRating")))
.withColumn("seniorLeadership", toDouble(raw("seniorLeadershipRating")))
.withColumn("careerOpportunities", toDouble(raw("careerOpportunitiesRating")))
.withColumn("cultureAndValues", toDouble(raw("cultureAndValuesRating")))
.withColumn("workLifeBalance", toDouble(raw("workLifeBalanceRating")))

val filtered = companies
.filter($"numberOfRatings" > 1)
.filter($"overallRating" >= 4)
.filter($"cultureAndValuesRating" >= 4)
.filter($"compensationAndBenefitsRating" >= 4)
