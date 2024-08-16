#!/bin/bash

MASTER_ADDRESS=${1:-"10.0.20.6"}
ETCD_SERVERS=${2:-"https://10.0.20.6:2379"}

KUBE_APISERVER_OPTS=" --logtostderr=true \
    --v=4 \
    --etcd-servers=https://10.0.20.6:2379 \
    --insecure-bind-address=127.0.0.1 \
    --bind-address=10.0.20.6 \
    --insecure-port=8080 \
    --secure-port=6443 \
    --advertise-address=10.0.20.6 \
    --allow-privileged=true \
    --service-cluster-ip-range=10.10.10.0/24 \
    --admission-control=NamespaceLifecycle,LimitRanger,SecurityContextDeny,ResourceQuota,NodeRestriction \
    --authorization-mode=RBAC,Node \
    --kubelet-https=true \
    --enable-bootstrap-token-auth \
    --token-auth-file=/ops_server/cfg/token.csv \
    --service-node-port-range=30000-50000 \
    --tls-cert-file=/ops_server/ssl/server.pem \
    --tls-private-key-file=/ops_server/ssl/server-key.pem \
    --client-ca-file=/ops_server/ssl/ca.pem \
    --service-account-key-file=/ops_server/ssl/ca-key.pem \
    --etcd-cafile=/ops_server/ssl/ca.pem \
    --etcd-certfile=/ops_server/ssl/server.pem \
    --etcd-keyfile=/ops_server/ssl/server-key.pem"
    
#该参数放配置文件, 读取不到, 临时使用export方式
export KUBE_APISERVER_OPTS=${KUBE_APISERVER_OPTS}

cat <<EOF >/ops_server/cfg/kube-apiserver
	KUBE_APISERVER_OPTS=${KUBE_APISERVER_OPTS}
EOF


cat <<EOF >/usr/lib/systemd/system/kube-apiserver.service
[Unit]
Description=Kubernetes API Server
Documentation=https://github.com/kubernetes/kubernetes

[Service]
EnvironmentFile=-/ops_server/cfg/kube-apiserver
#下面这行代码有可能读取不到配置文件





ExecStart=/ops_server/kubernetes/server/bin/kube-apiserver  --logtostderr=true --v=4 --etcd-servers=https://10.0.20.6:2379 --insecure-bind-address=127.0.0.1 --bind-address=10.0.20.6 --insecure-port=8080 --secure-port=6443 --advertise-address=10.0.20.6 --allow-privileged=true --service-cluster-ip-range=10.10.10.0/24  --kubelet-https=true --enable-bootstrap-token-auth --token-auth-file=/ops_server/cfg/token.csv --service-node-port-range=30000-50000 --tls-cert-file=/ops_server/ssl/server.pem --tls-private-key-file=/ops_server/ssl/server-key.pem --client-ca-file=/ops_server/ssl/ca.pem --service-account-key-file=/ops_server/ssl/ca-key.pem --etcd-cafile=/ops_server/ssl/ca.pem --etcd-certfile=/ops_server/ssl/server.pem --etcd-keyfile=/ops_server/ssl/server-key.pem  --enable-admission-plugins=LimitRanger,NamespaceExists,NamespaceLifecycle,ResourceQuota,ServiceAccount,DefaultStorageClass,MutatingAdmissionWebhook,DenyEscalatingExec --authorization-mode=RBAC,Node



Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable kube-apiserver
systemctl restart kube-apiserver
