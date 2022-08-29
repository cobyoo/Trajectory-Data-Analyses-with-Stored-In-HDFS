#!/bin/bash
start=20200423
end=20200424
# end 포함 안됨
while [ $start != $end ]
do
  echo $start
#  rm -r ./data/$start
#  mkdir ./data/$start
#  python wedrive.py $start
  node --max-old-space-size=32764 uploadver_sync.js $start > log/log$start
  start=$(date "-d $start 1 day" +%Y%m%d)
done
