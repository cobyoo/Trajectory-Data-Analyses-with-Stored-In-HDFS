
// import scala.io.Source 
// import scala.util.control._
// import org.apache.spark.{SparkConf, SparkContext}
// import org.apache.spark.rdd.RDD // RDD 형식 파일
// import org.apache.hadoop.fs.{FileSystem, Path, FileStatus} //hadoop FileSystem API
// import org.json.JSONObject //java maven
// import java.util.Arrays;
// import scala.runtime.ScalaRunTime._
// import org.apache.spark._
// import org.apache.spark.sql._

// object map_pipe { 
//     def main(args: Array[String]) : Unit = {

//         val conf = new SparkConf().setMaster("local").setAppName("My App")
//         val sc = new SparkContext(conf)
//         val sqlContext = new SQLContext(sc)
//         val point_hadoop_file_path = "/wedrive_data/raw_data/TB_TRACKING2_DATA_20200401.sql"
//         val point_RDD = data_process(point_hadoop_file_path, sc)
//         val point_pair_RDD = point_RDD.map(x => cut_string_point(x))
        
//         val df = point_pair_RDD.toDF("line")
//         val errors = df.filter(col("line").like("%ERROR%"))
//         errors.count()
//         errors.filter(col("line").like("%DataFrame%")).count()
//         errors.filter(col("line").like("%DataFrame%")).collect()

//         print("\n---------------------------------------------------------------------------\n")
     
//     }
//     def data_process (trajectory_hadoop_file_path : String, sc : SparkContext) : RDD[String] = {
//         val trajectory_RDD = sc.textFile(trajectory_hadoop_file_path) // RDD 생성
//         val insert_into_RDD = trajectory_RDD.filter(line => line.contains("INSERT INTO")) // INSERT INTO 문자열 검색 
//         val values_RDD = insert_into_RDD.map(line => line.split("VALUES ")(1)) // VALUES 문자열 쪼개기
//         val process_RDD = values_RDD.flatMap(line => line
//                                                         .replace("'", "").replace("", "").replace("\\", "").replace("\n", "")           
//                                                         .split(" " + "\n " + ' '))
//         return process_RDD
//     }

//     def cut_string_point (values_text : String) : (String, String) = {

//         var cut_text = values_text

//         if (cut_text(0) == '('){
//             cut_text = cut_text.slice(1,cut_text.length)
//         }
//         if (cut_text(cut_text.length-1) == ';'){
//             cut_text = cut_text.slice(0,cut_text.length-1)
//         }
//         if (cut_text(cut_text.length-1) == ')'){
//             cut_text = cut_text.slice(0 ,cut_text.length-1) 
//         }

//         val value_list = cut_text.split(",")
//         val uuid = value_list(0)
//         val begin_time = value_list(1)
//         val json_string_list = value_list.slice(2,value_list.length)
//         var json_string = json_string_list.mkString(",")
//         if (json_string(0) == '['){
//             json_string = json_string.slice(1, json_string.length)
//         }
//         if (json_string(json_string.length-1) == ']'){
//             json_string = json_string.slice(0, json_string.length-1)
//         }
//         try {
//             val whole_json_dict = new JSONObject(json_string)
//             val point_json_array = whole_json_dict.getJSONArray("items")
//             val point_string = point_json_array.toString()
//             val point_pair = (uuid + " " + begin_time, point_string)
//             return (uuid + " " + begin_time, point_string)
//         } catch {
//             case ex: Exception => return null
//         }
//     }
// }

