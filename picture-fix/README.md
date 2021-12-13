## 官方git

```shell
https://github.com/kubernetes-client/java
```



## 查看cluster role

```shell
kubectl get clusterrole
kubectl get sa
```



```shell
kubectl get clusterrolebindings.rbac.authorization.k8s.io
```



## 常用命令

```shell
1.获取节点和服务版本信息，并查看附加信息 kubectl get nodes -o wide
2.获取指定名称空间的pod kubectl get pod -n kube-system
3.查看pod的详细信息，以yaml格式或json格式显示
kubectl get pods -o yaml kubectl get pods -o json
4.查看pod的标签信息 kubectl get pod -A --show-labels
5.根据Selector（label query）来查询pod kubectl get pod -A --selector="k8s-app=kube-dns"
6.# 查看运行pod的环境变量 kubectl exec podName env
7.# 查看指定pod的日志 kubectl logs -f --tail 500 -n kube-system kube-apiserver-k8s-master
8.# 查看所有名称空间的service信息 kubectl get svc -A
9.# 查看componentstatuses信息 kubectl get cs
10.# 查看所有configmaps信息 kubectl get cm -A
11.# 查看所有serviceaccounts信息 kubectl get sa -A
12.# 查看所有daemonsets信息 kubectl get ds -A
13.# 查看所有deployments信息 kubectl get deploy -A
14.# 查看所有replicasets信息 kubectl get rs -A
15.# 查看所有statefulsets信息 kubectl get sts -A
16.# 查看所有jobs信息 kubectl get jobs -A
17.# 查看所有ingresses信息 kubectl get ing -A
18.# 查看有哪些名称空间 kubectl get ns
19.# 查看pod的描述信息 kubectl describe pod -n kube-system kube-apiserver-k8s-master
20.# 查看指定名称空间中指定deploy的描述信息 kubectl describe deploy -n kube-system coredns
21.# 查看node或pod的资源使用情况 kubectl top node 或 kubectl top pod
22.# 查看集群信息 kubectl cluster-info 或 kubectl cluster-info dump
23.# 查看各组件信息【172.16.1.110为master机器】
kubectl -s https://172.16.1.110:6443 get componentstatuses
24.#在不进入pod中执行bash命令 kubectl exec pod pod名 -n 命名空间 -- “ps -ef”
kubectl get pods --selector name=redis #按selector名来查找pod
kubectl get pods -o wide #查看pods所在的运行节点
kubectl get pods -o yaml #查看pods定义的详细信息
kubectl get nodes –lzone #获取zone的节点


```



```shell
#云服务器harbor密码: Login

```



## field selector

```shell
kubectl get pods --field-selector=status.phase!=Running,spec.restartPolicy=Always

kubectl get statefulsets,services --all-namespaces --field-selector metadata.namespace!=default


```



## 获取token

```shell

#1.创建admin user(必须创建admin user才能查看资源信息)
---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kube-system

#2.查看token
kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep admin-user | awk '{print $1}')

```

