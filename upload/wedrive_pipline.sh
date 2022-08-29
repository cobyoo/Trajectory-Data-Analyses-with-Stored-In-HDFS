rm -r ./data/$1
mkdir ./data/$1
python wedrive.py $1
node --max-old-space-size=32764 uploadver4.js > log/log$1
