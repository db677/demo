package com.hiya3d.picturefix.k8s;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hiya3d.picturefix.k8s.vo.deployment.ConditionsInfoVo;
import com.hiya3d.picturefix.k8s.vo.deployment.ContainerInfoVo;
import com.hiya3d.picturefix.k8s.vo.deployment.DeploymentInfoVo;
import com.hiya3d.picturefix.k8s.vo.deployment.VolumeMountVo;
import com.hiya3d.picturefix.k8s.vo.ingress.IngressInfoVo;
import com.hiya3d.picturefix.k8s.vo.node.NodeInfoVo;
import com.hiya3d.picturefix.k8s.vo.pod.PodConditionVo;
import com.hiya3d.picturefix.k8s.vo.pod.PodContainerVo;
import com.hiya3d.picturefix.k8s.vo.pod.PodInfoVo;
import com.hiya3d.picturefix.k8s.vo.pod.PodPortVo;
import com.hiya3d.picturefix.k8s.vo.pod.PodStatusVo;
import com.hiya3d.picturefix.k8s.vo.service.PortVo;
import com.hiya3d.picturefix.k8s.vo.service.ServiceInfoVo;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.models.ExtensionsV1beta1Ingress;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1ContainerState;
import io.kubernetes.client.openapi.models.V1ContainerStateWaiting;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentCondition;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1NodeSystemInfo;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodCondition;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.openapi.models.V1VolumeMount;

/**
 * 数据解析
 * 
 * @author Rex.Tan
 * @date 2021-12-9 15:07:24
 */
public class DataParseUtil {
	private static final BigDecimal D_1024 = new BigDecimal("1024");
	private static final String DATE_FMT = "yyyy-MM-dd HH:mm:ss";
	private static final ZoneId ZONE_GMT8 = ZoneId.of("GMT+08:00");
	
	/**
	 * 解析ingress信息
	 * @author Rex.Tan
	 * @date 2021-12-10 16:09:13
	 * @param info
	 * @param item
	 */
	public static void parseIngress(IngressInfoVo info, ExtensionsV1beta1Ingress item) {
		V1ObjectMeta meta = item.getMetadata();
		if(meta != null) {
			info.setCreationTimestamp(toStr(meta.getCreationTimestamp()));
			info.setName(meta.getName());
			info.setNamespace(meta.getNamespace());
			info.setResourceVersion(meta.getResourceVersion());
			info.setSelfLink(meta.getSelfLink());
		}
	}
	
	/**
	 * 解析pod信息
	 * @author Rex.Tan
	 * @date 2021-12-10 14:32:18
	 * @param info
	 * @param item
	 */
	public static void parsePodsInfo(PodInfoVo info, V1Pod item) {
		V1ObjectMeta meta = item.getMetadata();
		if(meta != null) {
			info.setCreationTimestamp(toStr(meta.getCreationTimestamp()));
			info.setName(meta.getName());
			info.setNamespace(meta.getNamespace());
			info.setResourceVersion(meta.getResourceVersion());
			info.setSelfLink(meta.getSelfLink());
		}
		V1PodSpec spec = item.getSpec();
		info.setNodeName(spec.getNodeName());
		/**
		 * containers
		 */
		List<PodContainerVo> containers = new LinkedList<>();
		if(spec != null && spec.getContainers() != null) {
			List<V1Container> containerList = spec.getContainers();
			for(V1Container ctr: containerList) {
				PodContainerVo pvo = new PodContainerVo();
				pvo.setImage(ctr.getImage());
				pvo.setName(ctr.getName());
				List<V1ContainerPort> portList = ctr.getPorts();
				List<PodPortVo> ports = new LinkedList<>();
				if(portList != null && !portList.isEmpty()) {
					for(V1ContainerPort port: portList) {
						ports.add(new PodPortVo(port.getContainerPort()));
					}
				}
				pvo.setPorts(ports);
				// resources
				V1ResourceRequirements  res = ctr.getResources();
				Map<String, Quantity> request = res.getRequests();
				Quantity resCpu = request.get("cpu");
				if(resCpu.getNumber() != null) {
					pvo.setResCpu(resCpu.getNumber());
				}
				Quantity resMemory = request.get("memory");
				if(resMemory.getNumber() != null) {
					pvo.setResMemory(resMemory.getNumber().divide(D_1024, 2, RoundingMode.UP).divide(D_1024, 2, RoundingMode.UP).intValue());
				}
				Map<String, Quantity> limit = res.getLimits();
				Quantity limitCpu = limit.get("cpu");
				if(limitCpu.getNumber() != null) {
					pvo.setLimitCpu(limitCpu.getNumber());
				}
				Quantity limitMemory = limit.get("memory");
				if(limitMemory.getNumber() != null) {
					pvo.setLimitMemory(limitMemory.getNumber().divide(D_1024, 2, RoundingMode.UP).divide(D_1024, 2, RoundingMode.UP).intValue());
				}
				// volumeMounts
				List<V1VolumeMount> mountList = ctr.getVolumeMounts();
				List<VolumeMountVo> volumeMounts = new LinkedList<>();
				if(mountList != null && !mountList.isEmpty()) {
					for(V1VolumeMount mount: mountList) {
						volumeMounts.add(new VolumeMountVo(mount.getName(), mount.getMountPath()));
					}
				}
				pvo.setVolumeMounts(volumeMounts);
				//
				containers.add(pvo);
			}
		}
		// status
		V1PodStatus status = item.getStatus();
		if(status != null) {
			info.setHostIp(status.getHostIP());
			info.setPhase(status.getPhase());
			info.setPodIp(status.getPodIP());
		}
		List<PodConditionVo> conditionVoList = new LinkedList<>();
		if(status != null && status.getConditions() != null) {
			List<V1PodCondition> conditionList = status.getConditions();
			for(V1PodCondition podCondition: conditionList) {
				if(PodInfoVo.ContainersReady.equals(podCondition.getType())) {
					info.setContainersStatus(podCondition.getStatus());
				}
				PodConditionVo pcVo = new PodConditionVo();
				pcVo.setMessage(podCondition.getMessage());
				pcVo.setType(podCondition.getType());
				pcVo.setReason(podCondition.getReason());
				pcVo.setStatus(podCondition.getStatus());
				conditionVoList.add(pcVo);
			}
		}
		info.setConditions(conditionVoList);
		List<PodStatusVo> podStatusList = new LinkedList<>();
		if(status != null && status.getContainerStatuses() != null) {
			List<V1ContainerStatus> statusList = status.getContainerStatuses();
			for(V1ContainerStatus constatus: statusList) {
				PodStatusVo podstatusVo = new PodStatusVo();
				podstatusVo.setImage(constatus.getImage());
				podstatusVo.setName(constatus.getName());
				podstatusVo.setReady(constatus.getReady());
				podstatusVo.setRestartCount(constatus.getRestartCount());
				podstatusVo.setStarted(constatus.getStarted());
				V1ContainerState state = constatus.getState();
				if(state != null  && state.getWaiting() != null) {
					V1ContainerStateWaiting waiting = state.getWaiting();
					podstatusVo.setReason(waiting.getMessage());
					podstatusVo.setMessage(waiting.getMessage());
				}
				podStatusList.add(podstatusVo);
			}
		}
		info.setPodStatusList(podStatusList);
		//
		info.setContainers(containers);
	}
	
	/**
	 * 解析service信息
	 * @author Rex.Tan
	 * @date 2021-12-10 13:46:43
	 * @param info
	 * @param item
	 */
	public static void parseServiceInfo(ServiceInfoVo info, V1Service item) {
		V1ObjectMeta meta = item.getMetadata();
		if(meta != null) {
			info.setCreationTimestamp(toStr(meta.getCreationTimestamp()));
			info.setName(meta.getName());
			info.setNamespace(meta.getNamespace());
			info.setResourceVersion(meta.getResourceVersion());
			info.setSelfLink(meta.getSelfLink());
		}
		V1ServiceSpec serviceSpec = item.getSpec();
		info.setClusterIp(serviceSpec.getClusterIP());
		info.setType(serviceSpec.getType());
		List<V1ServicePort> portList = serviceSpec.getPorts();
		List<PortVo> ports = new LinkedList<>();
		if(portList != null && !portList.isEmpty()) {
			for(V1ServicePort p: portList) {
				IntOrString istr = p.getTargetPort();
				Integer targetPort = 0;
				if(istr.isInteger()) {
					targetPort = istr.getIntValue();
				} else {
					targetPort = Integer.parseInt(istr.getStrValue());
				}
				ports.add(new PortVo(p.getPort(), p.getNodePort(), targetPort));
			}
		}
		info.setPortList(ports);
	}
	
	/**
	 * 解析deployment信息
	 * @author Rex.Tan
	 * @date 2021-12-10 9:51:07
	 * @param info
	 * @param item
	 */
	public static void parseDeploymentInfo(DeploymentInfoVo info, V1Deployment item) {
		V1ObjectMeta meta = item.getMetadata();
		if(meta != null) {
			info.setName(meta.getName());
			info.setNamespace(meta.getNamespace());
			info.setResourceVersion(meta.getResourceVersion());
			info.setSelfLink(meta.getSelfLink());
			info.setCreationTimestamp(toStr(meta.getCreationTimestamp()));
		}
		/**
		 * 副本数
		 */
		V1DeploymentSpec spec = item.getSpec();
		if(spec != null) {
			info.setReplicas(spec.getReplicas());
		}
		/**
		 * 更新类型
		 */
		if(spec != null && spec.getStrategy() != null) {
			info.setStrategyType(spec.getStrategy().getType());
		}
		/**
		 * 容器信息
		 */
		List<ContainerInfoVo> containerList = new LinkedList<>();
		if(spec != null && spec.getTemplate() != null) {
			V1PodTemplateSpec template = spec.getTemplate();
			List<V1Container> v1ContainerList = template.getSpec().getContainers();
			for(V1Container container: v1ContainerList) {
				ContainerInfoVo containerVo = new ContainerInfoVo();
				containerVo.setImage(container.getImage());
				containerVo.setName(container.getName());
				// 容器端口
				List<V1ContainerPort> portList = container.getPorts();
				StringBuilder portSb = new StringBuilder();
				for(V1ContainerPort port: portList) {
					if(port.getContainerPort() != null) {
						portSb.append(port.getContainerPort() + ",");
					}
				}
				containerVo.setContainerPort(portSb.toString().replaceFirst(",$", ""));
				// 资源
				V1ResourceRequirements res = container.getResources();
				Map<String, Quantity> request = res.getRequests();
				Quantity resCpu = request.get("cpu");
				if(resCpu.getNumber() != null) {
					containerVo.setResCpu(resCpu.getNumber());
				}
				Quantity resMemory = request.get("memory");
				if(resMemory.getNumber() != null) {
					containerVo.setResMemory(resMemory.getNumber().divide(D_1024, 2, RoundingMode.UP).divide(D_1024, 2, RoundingMode.UP).intValue());
				}
				Map<String, Quantity> limit = res.getLimits();
				Quantity limitCpu = limit.get("cpu");
				if(limitCpu.getNumber() != null) {
					containerVo.setLimitCpu(limitCpu.getNumber());
				}
				Quantity limitMemory = limit.get("memory");
				if(limitMemory.getNumber() != null) {
					containerVo.setLimitMemory(limitMemory.getNumber().divide(D_1024, 2, RoundingMode.UP).divide(D_1024, 2, RoundingMode.UP).intValue());
				}
				containerList.add(containerVo);
			}
		}
		info.setContainerList(containerList);
		/**
		 * conditions
		 */
		List<ConditionsInfoVo> conditionsList = new LinkedList<>();
		if(item.getStatus() != null && item.getStatus().getConditions() != null) {
			List<V1DeploymentCondition>  list = item.getStatus().getConditions();
			for(V1DeploymentCondition condition: list) {
				ConditionsInfoVo conditionVo = new ConditionsInfoVo();
				conditionVo.setLastTransitionTime(toStr(condition.getLastTransitionTime()));
				conditionVo.setLastUpdateTime(toStr(condition.getLastUpdateTime()));
				conditionVo.setMessage(condition.getMessage());
				conditionVo.setReason(condition.getReason());
				conditionVo.setType(condition.getType());
				conditionVo.setStatus(condition.getStatus());
				conditionsList.add(conditionVo);
			}
		}
		info.setConditionsList(conditionsList);
	}

	/**
	 * 解析node信息
	 * @author Rex.Tan
	 * @date 2021-12-10 9:50:54
	 * @param node
	 * @param item
	 */
	public static void parseNodeInfo(NodeInfoVo node, V1Node item) {
		/**
		 * metadata
		 */
		V1ObjectMeta meta = item.getMetadata();
		if (meta != null) {
			node.setName(meta.getName());
			node.setCreationTimestamp(toStr(meta.getCreationTimestamp()));
		}
		/**
		 * nodeInfo
		 */
		if (item.getStatus() != null && item.getStatus().getNodeInfo() != null) {
			V1NodeSystemInfo sysinfo = item.getStatus().getNodeInfo();
			node.setArchitecture(sysinfo.getArchitecture());
			node.setContainerRuntimeVersion(sysinfo.getContainerRuntimeVersion());
			node.setKernelVersion(sysinfo.getKernelVersion());
			node.setKubeletVersion(sysinfo.getKubeletVersion());
			node.setOperatingSystem(sysinfo.getOperatingSystem());
			node.setOsImage(sysinfo.getOsImage());
		}
		/**
		 * allocatable
		 */
		if (item.getStatus() != null && item.getStatus().getAllocatable() != null) {
			Map<String, Quantity> map = item.getStatus().getAllocatable();
			// cpu
			Quantity cpu = map.get("cpu");
			if (cpu != null) {
				node.setAllocatableCpu(cpu.getNumber());
			}
			// memory
			Quantity memory = map.get("memory");
			if (memory != null) {
				node.setAllocatableMemory(
						memory.getNumber().divide(D_1024, 2, RoundingMode.UP).divide(D_1024, 2, RoundingMode.UP));
			}
			// pods
			Quantity pods = map.get("pods");
			if (pods != null) {
				node.setAllocatablePods(pods.getNumber());
			}
		}
		/**
		 * capacity
		 */
		if (item.getStatus() != null && item.getStatus().getCapacity() != null) {
			Map<String, Quantity> map = item.getStatus().getCapacity();
			// cpu
			Quantity cpu = map.get("cpu");
			if (cpu != null) {
				node.setCapacityCpu(cpu.getNumber());
			}
			// memory
			Quantity memory = map.get("memory");
			if (memory != null) {
				node.setCapacityMemory(
						memory.getNumber().divide(D_1024, 2, RoundingMode.UP).divide(D_1024, 2, RoundingMode.UP));
			}
			// pods
			Quantity pods = map.get("pods");
			if (pods != null) {
				node.setCapacityPods(pods.getNumber());
			}
		}
	}

	/**
	 * 日期转为yyyy-MM-dd HH:mm:ss格式字符串
	 * @author Rex.Tan
	 * @date 2021-12-10 10:25:30
	 * @param time
	 * @return
	 */
	public static String toStr(OffsetDateTime time) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FMT).withZone(ZONE_GMT8);
		return dateTimeFormatter.format(time);
	}
	
	/**
	 * 单位转为mb
	 * @author Rex.Tan
	 * @date 2021-12-10 10:25:51
	 * @param size
	 * @return
	 */
	public static BigDecimal asMB(Integer size) {
		return new BigDecimal(size).multiply(D_1024).multiply(D_1024);
	}
	
	public static BigDecimal asMB(BigDecimal size) {
		return size.multiply(D_1024).multiply(D_1024);
	}

}
