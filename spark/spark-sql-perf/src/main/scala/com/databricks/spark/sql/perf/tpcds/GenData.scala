/*
 * Copyright 2015 Databricks Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.databricks.spark.sql.perf.tpcds

import scala.concurrent.Await
import scala.concurrent.duration._

import com.databricks.spark.sql.perf._
import com.databricks.spark.sql.perf.tpcds._
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

object GenData {
    def main(args: Array[String]) {
		if(args.length < 2){
			System.err.println("Usage: <inputPath> <scaleFactor>")
			System.exit(-1)
		}
	
		val conf = new SparkConf().setAppName("TPC-DS Bench")
		val sparkContext = new SparkContext(conf)
		val sqlContext = new SQLContext(sparkContext)
		
		val inputPath = args(0)
		val scaleFactor = args(1)
		
		// Tables in TPC-DS benchmark used by experiments.
		val tables = new TPCDSTables(sqlContext, "/home/git/tpsds-kit/tools", scaleFactor)
		
		tables.genData(inputPath, "parquet", true, false, false, false)
		
		sparkContext.stop()
    }
  }
