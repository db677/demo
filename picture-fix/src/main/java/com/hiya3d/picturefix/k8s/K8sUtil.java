package com.hiya3d.picturefix.k8s;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.hiya3d.picturefix.conf.exception.CustomException;
import com.hiya3d.picturefix.k8s.vo.deployment.DeploymentInfoVo;
import com.hiya3d.picturefix.k8s.vo.deployment.DeploymentReq;
import com.hiya3d.picturefix.k8s.vo.deployment.VolumeMountVo;
import com.hiya3d.picturefix.k8s.vo.ingress.IngressInfoVo;
import com.hiya3d.picturefix.k8s.vo.ingress.IngressReq;
import com.hiya3d.picturefix.k8s.vo.namespace.NamespaceInfoVo;
import com.hiya3d.picturefix.k8s.vo.node.NodeInfoVo;
import com.hiya3d.picturefix.k8s.vo.pod.PodInfoVo;
import com.hiya3d.picturefix.k8s.vo.service.ServiceInfoVo;
import com.hiya3d.picturefix.k8s.vo.service.ServiceReq;
import com.hiya3d.picturefix.utils.Assert;
import com.hiya3d.picturefix.utils.log.LogUtil;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.custom.Quantity.Format;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.apis.ExtensionsV1beta1Api;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1HTTPIngressPath;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1HTTPIngressRuleValue;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressBackend;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressList;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressRule;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1IngressSpec;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentList;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1DeploymentStrategy;
import io.kubernetes.client.openapi.models.V1HTTPGetAction;
import io.kubernetes.client.openapi.models.V1HostPathVolumeSource;
import io.kubernetes.client.openapi.models.V1LabelSelector;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1Probe;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.openapi.models.V1RollingUpdateDeployment;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;

public class K8sUtil {
	private static ApiClient client = null;
	
	/**
	 * 查询ingress
	 * @author Rex.Tan
	 * @date 2021-12-10 16:04:33
	 * @param namespace
	 * @return
	 */
	public static List<IngressInfoVo> findIngress(String namespace){
		ExtensionsV1beta1Api api = new ExtensionsV1beta1Api(getClient());
		List<IngressInfoVo> ingressList = new LinkedList<>();
		try {
			ExtensionsV1beta1IngressList result = api.listNamespacedIngress(namespace, null, null, null, null, null, null, null, null, null, null);
			List<ExtensionsV1beta1Ingress> list = result.getItems();
			for(ExtensionsV1beta1Ingress item: list) {
				IngressInfoVo info = new IngressInfoVo();
				DataParseUtil.parseIngress(info, item);
				ingressList.add(info);
			}
		} catch (ApiException e) {
			throwEx(e);
		}
		return ingressList;
	}
	
	/**
	 * 删除ingress
	 * @author Rex.Tan
	 * @date 2021-12-10 16:01:24
	 * @param req
	 * @return
	 */
	public static V1Status deleteIngress(IngressReq req) {
		ExtensionsV1beta1Api api = new ExtensionsV1beta1Api(getClient());
		try {
			return api.deleteNamespacedIngress(req.getName(), req.getNamespace(), null, null, null, null, null, null);
		} catch (ApiException e) {
			throwEx(e);
		}
		return null;
	}
	
	/**
	 * 创建ingress负载均衡
	 * @author Rex.Tan
	 * @date 2021-12-10 15:28:54
	 * @param req
	 */
	public static void createIngress(IngressReq req) {
		ExtensionsV1beta1Api api = new ExtensionsV1beta1Api(getClient());
		try {
			ExtensionsV1beta1Ingress ingress = new ExtensionsV1beta1Ingress();
			ingress.setApiVersion("extensions/v1beta1");
			ingress.setKind("Ingress");
			/**
			 * metadata
			 */
			V1ObjectMeta meta = new V1ObjectMeta();
			Map<String, String> annoMap = new HashMap<>();
			annoMap.put("nginx.ingress.kubernetes.io/cors-allow-headers", ">-DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization,token,channel,access_token");
			annoMap.put("nginx.ingress.kubernetes.io/cors-allow-methods", "PUT,GET,POST,OPTIONS,DELETE");
			annoMap.put("nginx.ingress.kubernetes.io/cors-allow-origin", "*");
			annoMap.put("nginx.ingress.kubernetes.io/enable-cors", "true");
			annoMap.put("nginx.ingress.kubernetes.io/service-weight", "");
			annoMap.put("nginx.ingress.kubernetes.io/proxy-body-size", "50m");
			annoMap.put("nginx.ingress.kubernetes.io/proxy-send-timeout", "300");
			annoMap.put("nginx.ingress.kubernetes.io/proxy-read-timeout", "300");
			annoMap.put("nginx.ingress.kubernetes.io/proxy-connect-timeout", "300");
			meta.setAnnotations(annoMap);
			meta.setName(req.getName());
			meta.setNamespace(req.getNamespace());
			ingress.setMetadata(meta);
			/**
			 * spec
			 */
			ExtensionsV1beta1IngressSpec spec = new ExtensionsV1beta1IngressSpec();
			// rules
			List<ExtensionsV1beta1IngressRule> rules = new LinkedList<>();
			ExtensionsV1beta1IngressRule rule = new ExtensionsV1beta1IngressRule();
			rule.setHost("hiya3d.com");
			// rules: http
			ExtensionsV1beta1HTTPIngressRuleValue http = new ExtensionsV1beta1HTTPIngressRuleValue();
			// rules: http: path
			List<ExtensionsV1beta1HTTPIngressPath> pathList = new LinkedList<>();
			ExtensionsV1beta1HTTPIngressPath path = new ExtensionsV1beta1HTTPIngressPath();
			path.setPath("/k8s");
			ExtensionsV1beta1IngressBackend backend = new ExtensionsV1beta1IngressBackend();
			backend.setServiceName(req.getName());
			backend.setServicePort(new IntOrString(req.getContainerPort()));
			path.setBackend(backend);
			pathList.add(path);
			http.setPaths(pathList);
			rule.setHttp(http);
			rules.add(rule);
			spec.setRules(rules);
			ingress.setSpec(spec);
			/**
			 * create
			 */
			api.createNamespacedIngress(req.getNamespace(), ingress, null, null, null);
		} catch (ApiException e) {
			throwEx(e);
		}
	}
	
	/**
	 * 查询pod
	 * @author Rex.Tan
	 * @date 2021-12-10 14:52:46
	 * @param namespace
	 * @return
	 */
	public static List<PodInfoVo> findPod(String namespace){
		CoreV1Api api = new CoreV1Api(getClient());
		List<PodInfoVo> podList = new LinkedList<>();
		try {
			V1PodList result = api.listNamespacedPod(namespace, null, null, null, null, null, null, null, null, null, null);
			List<V1Pod> list = result.getItems();
			if(list == null || list.isEmpty()) {
				return podList;
			}
			for(V1Pod item: list) {
				PodInfoVo info = new PodInfoVo();
				DataParseUtil.parsePodsInfo(info, item);
				podList.add(info);
			}
		} catch (ApiException e) {
			throwEx(e);
		}
		return podList;
	}
	
	/**
	 * 查询service
	 * @author Rex.Tan
	 * @date 2021-12-10 14:01:30
	 * @param namespace
	 * @return
	 */
	public static List<ServiceInfoVo> findService(String namespace) {
		CoreV1Api api = new CoreV1Api(getClient());
		List<ServiceInfoVo> serviceList = new LinkedList<>();
		try {
			V1ServiceList result = api.listNamespacedService(namespace, null, null, null, null, null, null, null, null,
					null, null);
			List<V1Service> list = result.getItems();
			if(list == null || list.isEmpty()) {
				return serviceList;
			}
			for(V1Service item: list) {
				ServiceInfoVo info = new ServiceInfoVo();
				DataParseUtil.parseServiceInfo(info, item);
				serviceList.add(info);
			}
		} catch (ApiException e) {
			throwEx(e);
		}
		return serviceList;
	}
	
	/**
	 * 删除service
	 * @author Rex.Tan
	 * @date 2021-12-10 13:41:25
	 * @param req
	 * @return
	 */
	public static V1Status deleteService(ServiceReq req) {
		CoreV1Api api = new CoreV1Api(getClient());
		try {
			return api.deleteNamespacedService(req.getName(), req.getNamespace(), null, null, null, null, null, null);
		} catch (ApiException e) {
			throwEx(e);
		}
		return null;
	}
	
	/**
	 * 创建service
	 * @author Rex.Tan
	 * @date 2021-12-10 13:34:35
	 * @param req
	 */
	public static void createService(ServiceReq req) {
		CoreV1Api api = new CoreV1Api(getClient());
		V1Service service = new V1Service();
		service.setApiVersion("v1");
		service.setKind("Service");
		/**
		 * metadata
		 */
		V1ObjectMeta meta = new V1ObjectMeta();
		meta.setName(req.getName());
		meta.setNamespace(req.getNamespace());
		service.setMetadata(meta);
		/**
		 * spec
		 */
		V1ServiceSpec serviceSpec = new V1ServiceSpec();
		/**
		 * spec: port
		 */
		List<V1ServicePort> ports = new LinkedList<>();
		V1ServicePort port = new V1ServicePort();
		port.setPort(req.getContainerPort());
		port.setTargetPort(new IntOrString(req.getContainerPort()));
		port.setProtocol("TCP");
		ports.add(port);
		serviceSpec.setPorts(ports);
		/**
		 * spec: selector
		 */
		Map<String, String> selector = new HashMap<>();
		selector.put("app", req.getName());
		serviceSpec.setSelector(selector);
		/**
		 * type
		 */
		serviceSpec.setType("ClusterIP");
		service.setSpec(serviceSpec);
		try {
			api.createNamespacedService(req.getNamespace(), service, null, null, null);
		} catch (ApiException e) {
			throwEx(e);
		}
	}
	
	/**
	 * 查询deployment列表
	 * @author Rex.Tan
	 * @date 2021-12-10 9:29:50
	 * @param namespace
	 * @return
	 */
	public static List<DeploymentInfoVo> findDeployment(String namespace) {
		AppsV1Api api = new AppsV1Api(getClient());
		List<DeploymentInfoVo> deployList = new LinkedList<>();
		try {
			V1DeploymentList result =  api.listNamespacedDeployment(namespace, null, null, null, null, null, null, null, null, null, null);
			List<V1Deployment> list = result.getItems();
			for(V1Deployment item: list) {
				DeploymentInfoVo info = new DeploymentInfoVo();
				DataParseUtil.parseDeploymentInfo(info, item);
				deployList.add(info);
			}
		} catch (ApiException e) {
			throwEx(e);
		}
		return deployList;
	}
	
	/**
	 * 删除deployment
	 * @author Rex.Tan
	 * @date 2021-12-10 9:13:50
	 * @param namespace
	 * @param deploymentName
	 * @return
	 */
	public static V1Status deleteDeployment(DeploymentReq req) {
		AppsV1Api api = new AppsV1Api(getClient());
		try {
			return api.deleteNamespacedDeployment(req.getName(), req.getNamespace(), null, null, null, null, null, null);
		} catch (ApiException e) {
			throwEx(e);
		}
		return null;
	}
	
	/**
	 * 创建deployment
	 * @author Rex.Tan
	 * @date 2021-12-9 17:06:20
	 * @param namespace
	 * @param req
	 * @return
	 */
	public static void createDeployment(DeploymentReq req) {
		AppsV1Api api = new AppsV1Api(getClient());
		V1Deployment deploy = buildDeploymentBody(req);
		try {
			api.createNamespacedDeployment(req.getNamespace(), deploy, null, null, null);
		} catch (ApiException e) {
			throwEx(e);
		}
	}
	
	/**
	 * 更新deployment
	 * @author Rex.Tan
	 * @date 2021-12-13 9:59:32
	 * @param req
	 */
	public static void updateDeployment(DeploymentReq req) {
		AppsV1Api api = new AppsV1Api(getClient());
		V1Deployment deploy = buildDeploymentBody(req);
		try {
			api.replaceNamespacedDeployment(req.getName(), req.getNamespace(), deploy, null, null, null);
		} catch (ApiException e) {
			throwEx(e);
		}
	}
	
	/**
	 * 构建deployment body
	 * @author Rex.Tan
	 * @date 2021-12-13 9:57:01
	 * @param req
	 * @return
	 */
	public static V1Deployment buildDeploymentBody(DeploymentReq req) {
		V1Deployment deploy = new V1Deployment();
		deploy.setApiVersion("apps/v1");
		deploy.setKind("Deployment");
		/**
		 * metadata
		 */
		V1ObjectMeta meta = new V1ObjectMeta();
		meta.setName(req.getName());
		meta.setNamespace(req.getNamespace());
		deploy.setMetadata(meta);
		/**
		 * spec
		 */
		V1DeploymentSpec spec = new V1DeploymentSpec();
		/**
		 * spec: selector
		 */
		V1LabelSelector selector = new V1LabelSelector();
		Map<String, String> matchLables = new HashMap<>();
		matchLables.put("app", req.getName());
		selector.setMatchLabels(matchLables);
		spec.setSelector(selector);
		/**
		 * spec: replicas
		 */
		spec.setReplicas(req.getReplicas());
		/**
		 * spec: strategy
		 */
		V1DeploymentStrategy strategy = new V1DeploymentStrategy();
		// 设置为滚动更新
		strategy.setType("RollingUpdate");
		V1RollingUpdateDeployment rollingUpdate = new V1RollingUpdateDeployment();
		// 升级过程中最多可以比原先设置多出的POD数量
		rollingUpdate.setMaxSurge(new IntOrString(1));
		// 升级过程中最多有多少个POD处于无法提供服务的状态
		rollingUpdate.setMaxUnavailable(new IntOrString(0));
		strategy.setRollingUpdate(rollingUpdate);
		spec.setStrategy(strategy);
		/**
		 * spec: template
		 */
		V1PodTemplateSpec template = new V1PodTemplateSpec();
		// metadata
		V1ObjectMeta templateMeta = new V1ObjectMeta();
		Map<String, String> templateMetaLabels = new HashMap<>();
		templateMetaLabels.put("app", req.getName());
		templateMeta.setLabels(templateMetaLabels);
		template.setMetadata(templateMeta);
		/**
		 * spec: template: spec
		 */
		V1PodSpec podSpec = new V1PodSpec();
		// Default: 从节点继承DNS相关配置
		podSpec.setDnsPolicy("Default");
		/**
		 * spec: template: spec: containers
		 */
		List<V1Container> podSpecContainers = new LinkedList<>();
		V1Container container = new V1Container();
		container.setName(req.getName());
		container.setImage(req.getImage());
		// 挂载目录
		if(req.getVolumeMounts() != null && !req.getVolumeMounts().isEmpty()) {
			List<V1VolumeMount> volumeMounts = new LinkedList<>();
			for(VolumeMountVo item: req.getVolumeMounts()) {
				V1VolumeMount mount = new V1VolumeMount();
				mount.setMountPath(item.getMountPath());
				mount.setName(item.getName());
				volumeMounts.add(mount);
			}
			container.setVolumeMounts(volumeMounts);
		}
		// containerPort
		List<V1ContainerPort> ports = new LinkedList<>();
		ports.add(new V1ContainerPort().containerPort(req.getContainerPort()));
		container.setPorts(ports);
		// 探针健康检查
		V1Probe probe = new V1Probe();
		V1HTTPGetAction httpGet = new V1HTTPGetAction();
		httpGet.setScheme("HTTP");
		httpGet.setPath(req.getHealthCheckPath());
		httpGet.setPort(new IntOrString(req.getContainerPort()));
		probe.setHttpGet(httpGet);
		// 第一次执行probe之前等待x秒钟
		probe.setInitialDelaySeconds(10);
		// 每隔x秒执行一次readness probe
		probe.setPeriodSeconds(10);
		container.setReadinessProbe(probe);
		// resources
		V1ResourceRequirements res = new V1ResourceRequirements();
		// request
		Map<String, Quantity> resMap = new HashMap<>();
		Quantity resMemory = new Quantity(DataParseUtil.asMB(req.getResMemory()), Format.DECIMAL_SI);
		resMap.put("memory", resMemory);
		// Quantity resCpu = new Quantity(DataParseUtil.asMB(req.getResCpu()), Format.DECIMAL_SI);
		Quantity resCpu = new Quantity(req.getResCpu(), Format.DECIMAL_SI);
		resMap.put("cpu", resCpu);
		res.setRequests(resMap);
		// limit
		Map<String, Quantity> limitMap = new HashMap<>();
		Quantity limitMemory = new Quantity(DataParseUtil.asMB(req.getLimitMemory()), Format.DECIMAL_SI);
		limitMap.put("memory", limitMemory);
		// Quantity limitCpu = new Quantity(DataParseUtil.asMB(req.getLimitCpu()), Format.DECIMAL_SI);
		Quantity limitCpu = new Quantity(req.getLimitCpu(), Format.DECIMAL_SI);
		limitMap.put("cpu", limitCpu);
		res.setLimits(limitMap);
		container.setResources(res);
		podSpecContainers.add(container);
		podSpec.setContainers(podSpecContainers);
		/**
		 * spec: template: spec: volumes
		 */
		if(req.getVolumeMounts() != null && !req.getVolumeMounts().isEmpty()) {
			List<V1Volume> volumes = new LinkedList<>();
			for(VolumeMountVo item: req.getVolumeMounts()) {
				V1Volume vol = new V1Volume();
				vol.setName(item.getName());
				V1HostPathVolumeSource hostPath = new V1HostPathVolumeSource();
				hostPath.setPath(item.getMountPath());
				hostPath.setType("DirectoryOrCreate");
				vol.setHostPath(hostPath);
				volumes.add(vol);
			}
			podSpec.setVolumes(volumes);
		}
		template.setSpec(podSpec);
		spec.setTemplate(template);
		/**
		 * create
		 */
		deploy.setSpec(spec);
		
		return deploy;
	}
	
	/**
	 * node列表查询
	 * @author Rex.Tan
	 * @date 2021-12-9 15:09:00
	 * @return
	 */
	public static List<NodeInfoVo> findNodes(){
		CoreV1Api api = new CoreV1Api(getClient());
		List<NodeInfoVo> nodeList = new LinkedList<>();
		try {
			V1NodeList result = api.listNode(null,null,null,null,null,null,null,null,null, null);
			Assert.notNull(result, "查询node列表出错, 返回null");
			List<V1Node> list = result.getItems();
			if(list == null || list.isEmpty()) {
				return nodeList;
			}
			for(V1Node item: list) {
				NodeInfoVo node = new NodeInfoVo();
				DataParseUtil.parseNodeInfo(node, item);
				nodeList.add(node);
			}
		} catch (ApiException e) {
			throwEx(e);
		}
		return nodeList;
	}
	
	/**
	 * 删除namespace
	 * @author Rex.Tan
	 * @date 2021-12-9 10:37:02
	 * @param namespace
	 * @return
	 */
	public static V1Status deleteNamespace(String namespace) {
		CoreV1Api api = new CoreV1Api(getClient());
		try {
			return api.deleteNamespace(namespace, null, null, null, null, null, null);
		} catch (ApiException e) {
			throwEx(e);
		}
		return null;
	}
	
	/**
	 * 创建namespace
	 * @author Rex.Tan
	 * @date 2021-12-9 10:15:16
	 * @param namespace
	 * @return
	 */
	public static void createNamespace(String namespace) {
		CoreV1Api api = new CoreV1Api(getClient());
		V1Namespace v1Namespace = new V1Namespace();
		v1Namespace.setKind("Namespace");
		V1ObjectMeta meta = new V1ObjectMeta();
		meta.setName(namespace);
		v1Namespace.setMetadata(meta);
		try {
			api.createNamespace(v1Namespace, null, null, null);
		} catch (ApiException e) {
			throwEx(e);
		}
	}

	/**
	 * 查询namespace列表
	 * 
	 * @author Rex.Tan
	 * @date 2021-12-9 9:54:47
	 * @return
	 */
	public static List<NamespaceInfoVo> findNamespaceList() {
		CoreV1Api api = new CoreV1Api(getClient());
		List<NamespaceInfoVo> nspList = new ArrayList<>();
		try {
			V1NamespaceList result = api.listNamespace(null, null, null, null, null, null, null, null, null, null);
			Assert.notNull(result, "查询namespace列表出错, 返回null");
			List<V1Namespace> list = result.getItems();
			if(list == null || list.isEmpty()) {
				return nspList;
			}
			for(V1Namespace item: list) {
				NamespaceInfoVo nsp = new NamespaceInfoVo();
				V1ObjectMeta meta = item.getMetadata();
				if(meta != null) {
					nsp.setCreationTimestamp(DataParseUtil.toStr(meta.getCreationTimestamp()));
					nsp.setName(meta.getName());
					nsp.setResourceVersion(meta.getResourceVersion());
					nsp.setSelfLink(meta.getSelfLink());
					nsp.setUid(meta.getUid());
				}
				if(item.getStatus() != null) {
					nsp.setPhase(item.getStatus().getPhase());
				}
				nspList.add(nsp);
			}
		} catch (ApiException e) {
			throwEx(e);
		}
		return nspList;
	}

	/**
	 * 初始化client
	 * 
	 * @author Rex.Tan
	 * @date 2021-12-8 15:13:12
	 * @return
	 */
	private static ApiClient getClient() {
		if (client == null) {
			return defaultClient();
		}
		return client;
	}

	private static ApiClient defaultClient() {
		try {
			client = new ClientBuilder().setBasePath("https://119.91.193.32:6443").setVerifyingSsl(false)
					.setAuthentication(new AccessTokenAuthentication(
							"eyJhbGciOiJSUzI1NiIsImtpZCI6IksxUU9NN0h5dXYwTGxEU3dlUTFXM0RxU2dUTE9BYTJiRm9CSm04ckVQYUUifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJrOHMtYXV0aG9yaXplLXRva2VuLW41bndqIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6Ims4cy1hdXRob3JpemUiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJjZDEwNjI0Yi05Y2YzLTQxODctYTdhMy0wZmMwNGJhZjE0MzEiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06azhzLWF1dGhvcml6ZSJ9.fyEWWm2NxCqRMc5WpZNf4AAXcuHAvC-y3RbrjfeEciHVXytl-LxwHOBn_wz_aSfx45N777yI3ttWoxcS-d_HL2Jgw3XaFn0qNQt7IHQ7z4a16iY6S9PwpjAAf9ngkZlrdVmYBiEBGkAHSmepH33ENrAJ0bDoUU-SC4ZEv9EASuewdOFscss9iKJPSO1IV6eRG3aXyX-lOhNWDfKv5Ja8--HLIMx-dTNlgtUKnPjzHeBLbN1eTHc_5_qD1zK_7CSuyy7arhS-VTRy5YWs3uBsba86b4bU63T8wdjAKW7skHtPRCuYjJaHsrIP4yzm7PGpPBojWRyh4NbRfKpk27--vA"))
					.build();
			Configuration.setDefaultApiClient(client);
			return client;
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.logException("初始化apiClient出错", e);
			throw new CustomException("初始化apiClient出错, " + e.getMessage());
		}
	}
	
	/**
	 * 统一处理接口异常
	 * @author Rex.Tan
	 * @date 2021-12-9 11:11:37
	 * @param e
	 */
	private static void throwEx(ApiException e) {
		e.printStackTrace();
		if(e.getResponseBody() == null) {
			throw new CustomException(e.getMessage());
		}
		JSONObject json = JSONObject.parseObject(e.getResponseBody());
		throw new CustomException(json.getString("message"));
	}
}

/**
 * Resource resource = new ClassPathResource("kubeconfig"); String tempPath =
 * System.getProperty("java.io.tmpdir") + "tomcat_" +
 * System.currentTimeMillis(); String tempFile = tempPath + File.separator +
 * "kubeconfig"; File file = new File(tempPath); if (!file.exists()) {
 * file.mkdirs(); } IOUtils.copy(resource.getInputStream(), new
 * FileOutputStream(new File(tempFile))); client =
 * ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new
 * FileReader(tempFile))).build(); Configuration.setDefaultApiClient(client);
 */
