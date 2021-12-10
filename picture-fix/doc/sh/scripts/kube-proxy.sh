#!/bin/bash

NODE_ADDRESS=${1:-"10.0.20.6"}

KUBE_PROXY_OPTS="--logtostderr=true \\
--v=4 \\
--hostname-override=${NODE_ADDRESS} \\
--kubeconfig=/ops_server/ssl/kube-proxy.kubeconfig"

export KUBE_PROXY_OPTS="${KUBE_PROXY_OPTS}"

cat <<EOF >/ops_server/cfg/kube-proxy

KUBE_PROXY_OPTS="${KUBE_PROXY_OPTS}"

EOF



cat <<EOF >/usr/lib/systemd/system/kube-proxy.service
[Unit]
Description=Kubernetes Proxy
After=network.target

[Service]
EnvironmentFile=-/ops_server/cfg/kube-proxy
ExecStart=/ops_server/kubernetes/server/bin/kube-proxy ${KUBE_PROXY_OPTS}
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable kube-proxy
systemctl restart kube-proxy
