# sparkLawCaseStorage
mongodb开启分片后，spark-mongo-connecter必须设置.config("spark.mongodb.input.partitioner","MongoShardedPartitioner")
submit spark集群运行程序，需设置.config("spark.driver.host"，""）
spark-mongo 写入时最好设置.config("spark.mongodb.output.maxBatchSize")，以配合
