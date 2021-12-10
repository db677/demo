
#!/bin/bash

MASTER_ADDRESS=${1:-"127.0.0.1"}

KUBE_CONTROLLER_MANAGER_OPTS="--logtostderr=true \
--v=4 \
--master=${MASTER_ADDRESS}:8080 \
--leader-elect=true \
--address=127.0.0.1 \
--service-cluster-ip-range=10.10.10.0/24 \
--cluster-name=kubernetes \
--cluster-signing-cert-file=/ops_server/ssl/ca.pem \
--cluster-signing-key-file=/ops_server/ssl/ca-key.pem \
--service-account-private-key-file=/ops_server/ssl/ca-key.pem \
--root-ca-file=/ops_server/ssl/ca.pem"

export KUBE_CONTROLLER_MANAGER_OPTS=${KUBE_CONTROLLER_MANAGER_OPTS}

cat <<EOF >/ops_server/cfg/kube-controller-manager
	KUBE_CONTROLLER_MANAGER_OPTS="${KUBE_CONTROLLER_MANAGER_OPTS}"
EOF


cat <<EOF >/usr/lib/systemd/system/kube-controller-manager.service
[Unit]
Description=Kubernetes Controller Manager
Documentation=https://github.com/kubernetes/kubernetes

[Service]
EnvironmentFile=-/ops_server/cfg/kube-controller-manager
ExecStart=/ops_server/kubernetes/server/bin/kube-controller-manager ${KUBE_CONTROLLER_MANAGER_OPTS}
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable kube-controller-manager
systemctl restart kube-controller-manager
