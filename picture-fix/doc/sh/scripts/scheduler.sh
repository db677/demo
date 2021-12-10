
#!/bin/bash

MASTER_ADDRESS=${1:-"127.0.0.1"}

KUBE_SCHEDULER_OPTS="--logtostderr=true \
--v=4 \
--master=${MASTER_ADDRESS}:8080 \
--leader-elect"

export KUBE_SCHEDULER_OPTS=${KUBE_SCHEDULER_OPTS}

cat <<EOF >/ops_server/cfg/kube-scheduler
	KUBE_SCHEDULER_OPTS=${KUBE_SCHEDULER_OPTS}
EOF



cat <<EOF >/usr/lib/systemd/system/kube-scheduler.service
[Unit]
Description=Kubernetes Scheduler
Documentation=https://github.com/kubernetes/kubernetes

[Service]
EnvironmentFile=-/ops_server/cfg/kube-scheduler
ExecStart=/ops_server/kubernetes/server/bin/kube-scheduler ${KUBE_SCHEDULER_OPTS}
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable kube-scheduler
systemctl restart kube-scheduler

