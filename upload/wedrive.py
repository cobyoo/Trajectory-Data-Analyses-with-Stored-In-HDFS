from hdfs import InsecureClient
import csv
import pandas as pd
import numpy as np
import time
import sys

def df_process():
    date = sys.argv[1]
    bt = time.time()
    csv.field_size_limit(1000000000)

    # namenode url
    client_hdfs = InsecureClient("http://ha-master:50070")

    read_path = "/ysh/{0}/type_1/".format(date)+client_hdfs.list("/ysh/{0}/type_1/".format(date))[1]
    with client_hdfs.read(read_path,encoding="utf-8") as r:
        df = pd.read_csv(r).astype('object').dropna(how='all')
        df.reset_index(drop=True, inplace=True)
        index = df[df['types']=='END'].index

        start = 0
        for i in index:
            df.loc[start:i,'accuracy':].to_csv("./data/"date+"/"+df.loc[i,'key']+".csv", index = False)
            start = i + 1

    et = time.time()-bt
    print("소요시간 : ", et)


def reader_process():
    start=time.time()
    csv.field_size_limit(1000000000)

    client_hdfs = InsecureClient("http://ha-master:50070")

    read_path = "/ysh/20200408/all/"+client_hdfs.list("/ysh/20200408/all/")[1]
    with client_hdfs.read(read_path,encoding="utf-8") as r:
        r_data = csv.reader(r)
        header = next(r_data)
        tmp = []
        for i in r_data:
            if i[-1] != 'END':
                tmp.append(i[1:])
            else:
                with open('/home/dblab/ysh/pipe_processing/sm/data/'+i[6]+'.csv', 'w') as f:
                    writer = csv.writer(f)
                    writer.writerow(header)
                    writer.writerows(tmp)
    end=time.time()-start
    print("소요시간 : ", end)


#reader_process()
df_process()
