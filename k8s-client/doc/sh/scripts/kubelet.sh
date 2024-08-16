#!/bin/bash

NODE_ADDRESS=${1:-"10.0.20.6"}
DNS_SERVER_IP=${2:-"10.10.10.2"}

KUBELET_OPTS="--logtostderr=true \\
--v=4 \\
--address=${NODE_ADDRESS} \\
--hostname-override=${NODE_ADDRESS} \\
--kubeconfig=/ops_server/ssl/kubelet.kubeconfig \\
--experimental-bootstrap-kubeconfig=/ops_server/ssl/bootstrap.kubeconfig \\
--cert-dir=/ops_server/ssl \\
--cluster-dns=${DNS_SERVER_IP} \\
--cluster-domain=cluster.local \\
--fail-swap-on=false \\
--pod-infra-container-image=registry.cn-hangzhou.aliyuncs.com/google-containers/pause-amd64:3.0"

export KUBELET_OPTS="${KUBELET_OPTS}"

cat <<EOF >/ops_server/cfg/kubelet

KUBELET_OPTS="${KUBELET_OPTS}"

EOF


cat <<EOF >/usr/lib/systemd/system/kubelet.service
[Unit]
Description=Kubernetes Kubelet
After=docker.service
Requires=docker.service

[Service]
EnvironmentFile=/ops_server/cfg/kubelet
ExecStart=/ops_server/kubernetes/server/bin/kubelet ${KUBELET_OPTS}
Restart=on-failure
KillMode=process

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable kubelet
systemctl restart kubelet
