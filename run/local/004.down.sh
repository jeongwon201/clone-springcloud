pid=`ps -ef|grep libs/eureka|grep -v grep | awk '{print $2}'`
kill -9 $pid

pid=`ps -ef|grep libs/composite|grep -v grep | awk '{print $2}'`
kill -9 $pid

pid=`ps -ef|grep libs/product|grep -v grep | awk '{print $2}'`
kill -9 $pid

pid=`ps -ef|grep libs/recommend|grep -v grep | awk '{print $2}'`
kill -9 $pid

pid=`ps -ef|grep libs/review|grep -v grep | awk '{print $2}'`
kill -9 $pid

docker stop mysqlDB mongoDB