
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

// object  pipe_processing { 
//     def main(args: Array[String]) : Unit = {

//         val conf = new SparkConf().setMaster("local").setAppName("My App")
//         val sc = new SparkContext(conf)
//         val sqlContext = new SQLContext(sc)
//         val point_hadoop_file_path = "/wedrive_data/raw_data/feature_DB_100k.json"
//         val point_RDD = data_process(point_hadoop_file_path, sc)

    

//         println("\n---------------------------------------------------------------------------\n")
//         point_RDD.take(1).foreach(println)
//     }
//     def data_process (trajectory_hadoop_file_path : String, sc : SparkContext) : RDD[String] = {
//         val trajectory_RDD = sc.textFile(trajectory_hadoop_file_path) // RDD 생성
//         // val insert_into_RDD = trajectory_RDD.filter(line => line.contains("INSERT INTO")) // INSERT INTO 문자열 검색 
//         // val values_RDD = insert_into_RDD.map(line => line.split("VALUES ")(1)) // VALUES 문자열 쪼개기
//         // val process_RDD = values_RDD.flatMap(line => line
//         //                                                 .replace("'", "").replace("", "").replace("\\", "").replace("\n", "")           
//         //                                                 .split(" " + "\n " + ' '))
//         return trajectory_RDD
//     }
// }

