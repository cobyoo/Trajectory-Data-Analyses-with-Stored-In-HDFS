# Trajectory-Data-Analyses-with-Stored-In-HDFS

![image](https://user-images.githubusercontent.com/39446946/188360771-d8fc3177-9a7d-4536-b496-ccaaa51eac0e.png)

## Index
- [Experiment Environment](#Experiment-Environment)
- [Requirements](#Requirements)

## Experiment Environment
    - Master 1 Unit Node
        - OS : Ubuntu 20.04 LTS
        - SSD : Samsung SSD 980 PRO 2TB 
        - CPU : Intel(R) Xeon(R) Silver 4214R CPU @ 2.40GHz
        - Core : 48
        - Memory : DDR4, 128GB
        - Spark :  3.1.1
        - Hadoop : 3.2.2
        
    - Worker 3 Unit Node
        - OS : Ubuntu 20.04 LTS
        - SSD : Samsung SSD 980 PRO 1TB 
        - CPU : Intel(R) Xeon(R) Silver 4210 CPU @ 2.40GHz
        - Core : 120
        - Memory : DDR4, 500GB
        - Spark :  3.1.1
        - Hadoop : 3.2.2

## Requirements
        1    for(record <- hdfs_path_point_data)
        2              val point_data_dict = flatMap[String,String]()
        3              if (record("INSERT INTO"))     
        4                     record = record.stringcut()
        5                     val trajectory_list = recode.split(delimiter)
        6                     map(trajectory <- trajectoryt_list)
        7                           val sql_str = trajectory.split(delimiter)
        8                           point_data_dict(key: begin time + uuid(in sql_str)) = value : point_list


