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

object TestSQL {
    def main(args: Array[String]) {
		if(args.length < 3){
			System.err.println("Usage: <inputPath> <outputPath> <iterations>")
			System.exit(-1)
		}
	
		val conf = new SparkConf().setAppName("TPC-DS Bench")
		val sparkContext = new SparkContext(conf)
		val sqlContext = new SQLContext(sparkContext)
		
		val inputPath = args(0)
		val outputPath = args(1)
		val iterations = args(2)
		
		// Tables in TPC-DS benchmark used by experiments.
		//val tables = new TPCDSTables(sqlContext, "/home/git/tpsds-kit/tools", "1")
		
		//tables.genData(inputPath, "parquet", true, false, false, false)
	
		val tableNames = Array("call_center", 
	                       "catalog_page", 
	                       "catalog_returns", 
	                       "catalog_sales", 
	                       "customer", 
	                       "customer_address", 
	                       "customer_demographics", 
	                       "date_dim", 
	                       "household_demographics", 
	                       "income_band", 
	                       "inventory", 
	                       "item", 
	                       "promotion", 
	                       "reason", 
	                       "ship_mode", 
	                       "store", 
	                       "store_returns", 
	                       "store_sales", 
	                       "time_dim", 
	                       "warehouse", 
	                       "web_page", 
	                       "web_returns", 
	                       "web_sales", 
						   "web_site")
						   
	    for(i <- 0 to tableNames.length - 1) {
			sqlContext.read.parquet(inputPath + "/" + tableNames{i}).registerTempTable(tableNames{i})
		}

		// Setup TPC-DS experiment
		val tpcds = new TPCDS (sqlContext = sqlContext)
	    
		val experiment = tpcds.runExperiment(tpcds.tpcds1_4Queries, iterations = iterations.toInt, resultLocation = outputPath)
		experiment.waitForFinish(60*60*10)
	
		sparkContext.stop()
    }
  }
