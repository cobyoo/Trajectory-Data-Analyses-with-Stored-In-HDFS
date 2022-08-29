//2021.05.06 created by ysh 

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

object DataRoad_Saved{  

    def main(args: Array[String]) : Unit = {
       
        val conf = new SparkConf().setMaster("yarn").setAppName("My App")
        val sc = new SparkContext(conf)
        val point_hadoop_file_path = "/wedrive_data/raw_data/TB_TRACKING2_DATA_20200401.sql"

        val point_RDD = data_process(point_hadoop_file_path, sc)
        val point_pair_RDD = point_RDD.map(x => cut_string_point(x))
       
        // val map_point = data.split(",").map( x=> {
        // val y = x.split(":");(y(0),y(1)) } ).map(x=>(x._1,x._2)).toMap

        //map_point.take(10).foreach(println)

        val sqlContext = new org.apache.spark.sql.SQLContext(sc)
        import sqlContext.implicits._   

        val fs = FileSystem.get(sc.hadoopConfiguration)
        val df = sqlContext.read.json(point_pair_RDD).drop("address")
        //df.coalesce(1).sort($"key", $"id").write.format("com.databricks.spark.csv").option("header","true").save("/ysh/20200401/all")
        
        val correct_type = df.where($"accuracy_x".isNull && $"accuracy_y".isNull && $"date".isNull)
        val correct_type2 = correct_type.select($"key",$"accuracy",$"id",$"lat",$"lng",$"speed",$"time",$"types")
        
        //correct_type2.show(20,false)
        //val correct_type2 = df.select($"key",$"accuracy",$"id",$"lat",$"lng",$"speed",$"time",$"types")
        correct_type2.coalesce(1).sort($"key", $"id").write.format("com.databricks.spark.csv").option("header","true").save("/ysh/20200401/type_1")
        // val incorrect_type = df.where($"accuracy".isNull && $"accuracy_x".isNotNull)
        // val incorrect_type2 = incorrect_type.select($"key",$"date",$"types",$"lat",$"lng",$"speed",$"accuracy_x",$"accuracy_y")
        // incorrect_type2.coalesce(1).sort($"key", $"id").write.format("com.databricks.spark.csv").option("header","true").save("/ysh/20200401/type_2")

          
        // val correct_type_uuid_list = correct_type.select($"key").distinct.collect().toList
        // // println(correct_type_uuid_list.length)
        // for (i <- correct_type_uuid_list){
        //     var temp = i.toString()
        //     var filename = temp.substring(1,temp.length-1)
        //     var df2 = correct_type.select($"*").filter($"key" === filename)
        //     var df4 = df2.select($"accuracy",$"id",$"lat",$"lng",$"speed",$"time",$"types")
        //     //println(filename)
        //     //df3.show(2,false)
        //     println("type 1", new Date(System.currentTimeMillis()))
        //     df4.show(1)
        //     var j = 0
        //     df4.repartition(1).write.format("com.databricks.spark.csv").option("header","true").save("/ysh/20200406/"+filename)
        // }
        
        // val incorrect_type = df.where($"accuracy".isNull && $"accuracy_x".isNotNull)
        // val incorrect_type_uuid_list = incorrect_type.select($"key").distinct.collect().toList
        /*
        println(incorrect_type_uuid_list.length)
       
        for (i <- incorrect_type_uuid_list){
            var temp2 = i.toString()
            var filename2 = temp2.substring(1,temp2.length-1)
            var df2_2 = incorrect_type.select($"*").filter($"key" === filename2)
            var df3 = df2_2.
            // if (df2.select($"accuracy").first.getString(0).toString eq null){
            //println(filename2)
            //df3.show(2,false)
            println("type 2", new Date(System.currentTimeMillis()))
            df3.show(1)  
            df3.repartition(1).write.format("com.databricks.spark.csv").option("header","true").save("/ysh/20200406_incorrect/"+filename)
          */
        }
        print("\n-----------------------------------------------------------------------------------------------------------\n")
        
    def data_process (trajectory_hadoop_file_path : String, sc : SparkContext) : RDD[String] = {
        val trajectory_RDD = sc.textFile(trajectory_hadoop_file_path) // RDD 생성
        val insert_into_RDD = trajectory_RDD.filter(line => line.contains("INSERT INTO")) // INSERT INTO 문자열 검색 
        val values_RDD = insert_into_RDD.map(line => line.split("VALUES ")(1)) // VALUES 문자열 쪼개기
        val process_RDD = values_RDD.flatMap(line => line
                                                        .replace("'", "").replace("", "").replace("\\", "").replace("\n", "").replace("type","types")
                                                        .split(" " + "\n " + ' '))
        return process_RDD
    }
    def cut_string_point (values_text : String) : String = {
        var cut_text = values_text
        //return cut_text
        if (cut_text(0) == '('){ 
            cut_text = cut_text.slice(1,cut_text.length) 
        }
        if (cut_text(cut_text.length-1) == ';'){ 
            cut_text = cut_text.slice(0,cut_text.length-1)
        }
        if (cut_text(cut_text.length-1) == ')'){
            cut_text = cut_text.slice(0 ,cut_text.length-1) 
        }

        val value_list = cut_text.split("\\),")
        val rlen = value_list.map(x => x.slice(0, x.length-1))
        try{        
        val whole_string = rlen.map{ line=>
           
            var idx = line.indexOf("[")

            var key = line.slice(0, idx-1).toString
            var body = line.slice(idx+1, line.length).toString  //json -> object
            val whole_json_obj = new JSONObject(body)
            val t1 = whole_json_obj.getJSONArray("items")
            
            var tlen = t1.length()

            val json_arr = (0 until tlen).map{ idx =>
                var v1 = t1.getJSONObject(idx)
                //(000011215a38400fae14f7c1586a3b3b,2020-04-01 07:14:41
                //20200401_08f2f7ba0d4b4635b1c36ca80de51078_20200401200446

                var kdata=key.split(",")
                var uuid=kdata(0)
                var bt = kdata(1)
                val id_val = bt.substring(0,10).replace("-","")+"_" + uuid.replace("(","") + "_" + bt.replace("-","").replace(" ","").replace(":","")
                v1.put("key",id_val)
                var res = v1.toString
                res
            }
            var res = json_arr.mkString(",")
            // var dict = (key,res)
            // dict
            res
        }
        val test=whole_string.mkString(",")
    
        return "["+test+"]"
    } catch {
         case ex: Exception => return null
    }
    }    
}
