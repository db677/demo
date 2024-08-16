#!/bin/bash

start(){
    cd /ds
    # 删除已存在的镜像
    docker rmi k8s-test_docker
    # 构建镜像
    docker build -f /ds/picture-fix-dockerfile -t k8s-test_docker .

    # 获取时间戳
    current=`date "+%Y-%m-%d %H:%M:%S"`
    timeStamp=`date -d "$current" +%s` 
    #将current转换为时间戳，精确到毫秒
    currentTimeStamp=$((timeStamp*1000+10#`date "+%N"`/1000000)) 
    #echo $currentTimeStamp
    export k8s-test_v=$currentTimeStamp

    # 添加tag并推送到本地仓库
    docker tag k8s-test_docker:latest 10.0.20.6:60001/kubernetes/k8s-test_docker:$k8s-test_v
    docker push 10.0.20.6:60001/kubernetes/k8s-test_docker:$k8s-test_v


    #kubectl apply -f ./k8s-test.yaml
}

stop(){
    # kill -9 `ps -ef | grep "$command" | awk '{print $2}'`
    echo 'stop'
}

case "$1" in  
start)  
start
;; 

stop)
stop
;;

esac
