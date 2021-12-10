#!/bin/bash

start(){
    cd /ds
    # 删除已存在的镜像
    docker rmi picture_fix_docker
    # 构建镜像
    docker build -f /ds/picture-fix-dockerfile -t picture_fix_docker .

    # 获取时间戳
    current=`date "+%Y-%m-%d %H:%M:%S"`
    timeStamp=`date -d "$current" +%s` 
    #将current转换为时间戳，精确到毫秒
    currentTimeStamp=$((timeStamp*1000+10#`date "+%N"`/1000000)) 
    #echo $currentTimeStamp
    export picture_fix_v=$currentTimeStamp

    # 添加tag并推送到本地仓库
    docker tag picture_fix_docker:latest 10.0.20.6:60001/kubernetes/picture_fix_docker:$picture_fix_v
    docker push 10.0.20.6:60001/kubernetes/picture_fix_docker:$picture_fix_v

    # 启动
    # docker run -id --name=wechat_docker_01 -p 9999:9999 -v /deploy/logs:/deploy/logs wechat_docker
    #cd /ds/marketouch/yaml
    # 修改版本号
    #sed -i "s/project_manage_server_docker\:.*$/project_manage_server_docker:$project_manage_server_v/" ./project-manage-server.yaml
    #kubectl create configmap special-config --from-literal=special.image_version=$material_management_v

    #kubectl apply -f ./project-manage-server.yaml
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
