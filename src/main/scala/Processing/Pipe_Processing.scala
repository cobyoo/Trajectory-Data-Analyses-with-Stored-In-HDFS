//2021.04.28 created by ysh 

import scala.io.Source 
import scala.util.control._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD // RDD 형식 파일
import org.apache.hadoop.fs.{FileSystem, Path, FileStatus} //hadoop FileSystem API
import org.json.JSONObject //java maven
import java.util.Arrays;
import scala.runtime.ScalaRunTime._
import org.apache.spark._
import org.apache.spark.sql.{SaveMode,Dataset,Row,SparkSession}
import scala.annotation.meta.param
import org.apache.hadoop.fs._
import org.json4s.jackson.Serialization
import java.util.Date
import java.util.concurrent.TimeUnit.NANOSECONDS

 object Pipe_Processing{ 
     
     def main(args: Array[String]) : Unit = {
        val conf = new SparkConf().setMaster("yarn").setAppName("My App")
        val sc = new SparkContext(conf)
        val trajectory_hadoop_file_path = "/wedrive_data/raw_data/TB_TRACKING2_SIMPLE_20200401.sql"
        val point_hadoop_file_path = "/wedrive_data/raw_data/TB_TRACKING2_DATA_20200401.sql"
            
        val sqlContext = new org.apache.spark.sql.SQLContext(sc)
        import sqlContext.implicits._

        val trajectory_RDD = data_process(trajectory_hadoop_file_path, sc)
        val point_RDD = data_process(point_hadoop_file_path, sc) 
       
        
        var start = System.nanoTime() //나노 초로 시간 측정 
        
        val trajectory_pair_RDD = trajectory_RDD.map(x => cut_string_trajectory(x)) 
        val point_pair_RDD = point_RDD.map(x => cut_string_point(x))

        val joined_RDD = trajectory_pair_RDD.join(point_pair_RDD)
        println("--------------------------------------------------------------------------------------------------")
        // // println("\n trajectory_pair_RDD \n")
        trajectory_RDD.collect.foreach(array => println(stringOf(array)))
        //trajectory_pair_RDD.collect.foreach(array => println(stringOf(array)))
        //println("\n point_pair_RDD \n")
        //trajectory_pair_RDD.take(1).foreach(println)
        // println("\n joined_RDD \n")
        //joined_RDD.collect.foreach(x => println(stringOf(x)))
        println("--------------------------------------------------------------------------------------------------")
        var end = System.nanoTime() // 나노 초로 끝 시간 측정

     }
     def data_process (trajectory_hadoop_file_path : String, sc : SparkContext) : RDD[String] = {
         val trajectory_RDD = sc.textFile(trajectory_hadoop_file_path) // RDD 생성
         val insert_into_RDD = trajectory_RDD.filter(line => line.contains("INSERT INTO")) // INSERT INTO 문자열 검색 
         val values_RDD = insert_into_RDD.map(line => line.split("VALUES ")(1)) // VALUES 문자열 쪼개기
         val process_RDD = values_RDD.flatMap(line => line.replace("'", "").replace("", "").replace("\\", "").replace("\n", "").split(" " + "\n}," + ' '))
         // val process_RDD = values_RDD.flatMap(line => line.replace("'", "").replace("", "").replace("\\", "").replace("\n", "").split({"\n,"}))  
         return process_RDD
     }
     
     def cut_string_trajectory (values_text : String) : (String, Array[String]) = {
     // def cut_string_trajectory (values_text : String) : String = {
         var cut_text = values_text

         if (cut_text(0) == '('){
             cut_text = cut_text.slice(1, cut_text.length)
         }
         if (cut_text(cut_text.length-1) == ';'){
             cut_text = cut_text.slice(0,cut_text.length-1)
         }
         if (cut_text(cut_text.length-1) == ')'){
             cut_text = cut_text.slice(0,cut_text.length-1)
         }

         val value_list = cut_text.split(",")
         val uuid = value_list(0)
         val begin_time = value_list(1)
         val trajectory_pair = (uuid + "," + begin_time, value_list)
         return (uuid + "," + begin_time, value_list)
     }

     def cut_string_point (values_text : String) : (String, String) = {
     // def cut_string_point (values_text : String) : Array = {
         var cut_text = values_text

         if (cut_text(0) == '('){
             cut_text = cut_text.slice(1,cut_text.length)
         }
         if (cut_text(cut_text.length-1) == ';'){
             cut_text = cut_text.slice(0,cut_text.length-1)
         }
         if (cut_text(cut_text.length-1) == ')'){
             cut_text = cut_text.slice(0 ,cut_text.length-1) 
         }

         val value_list = cut_text.split(",")
         val uuid = value_list(0)
         val begin_time = value_list(1)
         val json_string_list = value_list.slice(2,value_list.length)
         var json_string = json_string_list.mkString(",")
         if (json_string(0) == '['){
             json_string = json_string.slice(1, json_string.length)
         }
         if (json_string(json_string.length-1) == ']'){
             json_string = json_string.slice(0, json_string.length-1)
         }
    
         try {
             val whole_json_dict = new JSONObject(json_string)
             val point_json_array = whole_json_dict.getJSONArray("items")
             val point_string = point_json_array.toString()
             val point_pair = (uuid + "," + begin_time, point_string)
             return (uuid + "," + begin_time, point_string)
         } catch {
             case ex: Exception => return null
         }
     }
     
 }
