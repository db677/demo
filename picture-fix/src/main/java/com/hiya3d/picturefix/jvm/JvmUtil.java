package com.hiya3d.picturefix.jvm;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.sun.management.OperatingSystemMXBean;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

@SuppressWarnings("all")
@Component
public class JvmUtil {

	public Map<String, Object> env() {
		Map<String, Object> result = new HashMap<>();
		Runtime r = Runtime.getRuntime();
		Properties props = System.getProperties();
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String ip = addr.getHostAddress();
		Map<String, String> map = System.getenv();
		String userName = map.get("USERNAME");// 获取用户名
		String computerName = map.get("COMPUTERNAME");// 获取计算机名
		String userDomain = map.get("USERDOMAIN");// 获取计算机域名
		result.put("用户名", userName);
		result.put("计算机名", computerName);
		result.put("计算机域名", userDomain);
		result.put("本地ip地址", ip);
		result.put("本地主机名", addr.getHostName());
		result.put("JVM可以使用的总内存", new DecimalFormat("#.##").format(r.totalMemory() / 1024 / 1024) + "M");
		result.put("JVM可以使用的剩余内存", new DecimalFormat("#.##").format(r.freeMemory() / 1024 / 1024) + "M");
		result.put("JVM可以使用的处理器个数", r.availableProcessors());
		result.put("Java的运行环境版本 ", props.getProperty("java.version"));
		result.put("Java的运行环境供应商 ", props.getProperty("java.vendor"));
		result.put("Java供应商的URL ", props.getProperty("java.vendor.url"));
		result.put("Java的安装路径 ", props.getProperty("java.home"));
		result.put("Java的虚拟机规范版本 ", props.getProperty("java.vm.specification.version"));
		result.put("Java的虚拟机规范供应商 ", props.getProperty("java.vm.specification.vendor"));
		result.put("Java的虚拟机规范名称 ", props.getProperty("java.vm.specification.name"));
		result.put("Java的虚拟机实现版本 ", props.getProperty("java.vm.version"));
		result.put("Java的虚拟机实现供应商 ", props.getProperty("java.vm.vendor"));
		result.put("Java的虚拟机实现名称 ", props.getProperty("java.vm.name"));
		result.put("Java运行时环境规范版本 ", props.getProperty("java.specification.version"));
		result.put("Java运行时环境规范供应商 ", props.getProperty("java.specification.vender"));
		result.put("Java运行时环境规范名称 ", props.getProperty("java.specification.name"));
		result.put("Java的类格式版本号 ", props.getProperty("java.class.version"));
		result.put("Java的类路径 ", props.getProperty("java.class.path"));
		result.put("加载库时搜索的路径列表 ", props.getProperty("java.library.path"));
		result.put("默认的临时文件路径 ", props.getProperty("java.io.tmpdir"));
		result.put("一个或多个扩展目录的路径 ", props.getProperty("java.ext.dirs"));
		result.put("操作系统的名称 ", props.getProperty("os.name"));
		result.put("操作系统的构架 ", props.getProperty("os.arch"));
		result.put("操作系统的版本 ", props.getProperty("os.version"));
		result.put("文件分隔符 ", props.getProperty("file.separator"));
		result.put("路径分隔符 ", props.getProperty("path.separator"));
		result.put("行分隔符 ", props.getProperty("line.separator"));
		result.put("用户的账户名称 ", props.getProperty("user.name"));
		result.put("用户的主目录 ", props.getProperty("user.home"));
		result.put("用户的当前工作目录 ", props.getProperty("user.dir"));
		return result;
	}

	public Map<String, Object> mem() {
		Map<String, Object> result = new HashMap<>();
		OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
		// 堆内存使用情况
		MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
		// 初始的总内存
		long initTotalMemorySize = memoryUsage.getInit();
		// 最大可用内存
		long maxMemorySize = memoryUsage.getMax();
		// 已使用的内存
		long usedMemorySize = memoryUsage.getUsed();
		Map<String, Object> memoryUsageMap = new HashMap<>();
		memoryUsageMap.put("初始的总内存", new DecimalFormat("#.##").format(initTotalMemorySize / 1024 / 1024) + "M");
		memoryUsageMap.put("最大可用内存", new DecimalFormat("#.##").format(maxMemorySize / 1024 / 1024) + "M");
		memoryUsageMap.put("已使用的内存", new DecimalFormat("#.##").format(usedMemorySize / 1024 / 1024) + "M");
		// 总的物理内存
		String totalMemorySize = new DecimalFormat("#.##").format(osmxb.getTotalPhysicalMemorySize() / 1024.0 / 1024)
				+ "M";
		// 剩余的物理内存
		String freePhysicalMemorySize = new DecimalFormat("#.##")
				.format(osmxb.getFreePhysicalMemorySize() / 1024.0 / 1024) + "M";
		// 已使用的物理内存
		String usedMemory = new DecimalFormat("#.##")
				.format((osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / 1024.0 / 1024) + "M";
		memoryUsageMap.put("总的物理内存", totalMemorySize);
		memoryUsageMap.put("剩余的物理内存", freePhysicalMemorySize);
		memoryUsageMap.put("已使用的物理内存", usedMemory);
		String jvmInitMem = new DecimalFormat("#.#").format(initTotalMemorySize * 1.0 / 1024 / 1024) + "M";
		String jvmMaxMem = new DecimalFormat("#.#").format(maxMemorySize * 1.0 / 1024 / 1024) + "M";
		String jvmUsedMem = new DecimalFormat("#.#").format(usedMemorySize * 1.0 / 1024 / 1024) + "M";
		memoryUsageMap.put("JVM初始总内存", jvmInitMem);
		memoryUsageMap.put("JVM最大可用内存", jvmMaxMem);
		memoryUsageMap.put("JVM已使用的内存", jvmUsedMem);
		result.put("系统内存使用情况", memoryUsageMap);
		return result;
	}

	public Map<String, Object> cpu() {
		Map<String, Object> result = new HashMap<>();
		SystemInfo systemInfo = new SystemInfo();
		result.put("程序启动时间", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(ManagementFactory.getRuntimeMXBean().getStartTime())));
		result.put("程序更新时间", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date(ManagementFactory.getRuntimeMXBean().getUptime())));
		CentralProcessor processor = systemInfo.getHardware().getProcessor();
		long[] prevTicks = processor.getSystemCpuLoadTicks();
		long[] ticks = processor.getSystemCpuLoadTicks();
		long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
				- prevTicks[CentralProcessor.TickType.NICE.getIndex()];
		long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
				- prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
		long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
				- prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
		long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
				- prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
		long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
				- prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
		long user = ticks[CentralProcessor.TickType.USER.getIndex()]
				- prevTicks[CentralProcessor.TickType.USER.getIndex()];
		long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
				- prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
		long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
				- prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
		long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
		result.put("cpu核数", processor.getLogicalProcessorCount());
		result.put("cpu系统使用率", new DecimalFormat("#.##%").format(cSys * 1.0 / totalCpu));
		result.put("cpu用户使用率", new DecimalFormat("#.##%").format(user * 1.0 / totalCpu));
		result.put("cpu当前等待率", new DecimalFormat("#.##%").format(iowait * 1.0 / totalCpu));
		result.put("cpu当前空闲率", new DecimalFormat("#.##%").format(idle * 1.0 / totalCpu));
		return result;
	}

	public Map<String, Object> jvm() {
		Map<String, Object> result = new HashMap<>();
		// 获得线程总数
		ThreadGroup parentThread;
		for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
				.getParent() != null; parentThread = parentThread.getParent()) {
		}
		int totalThread = parentThread.activeCount();
		result.put("总线程数", totalThread);
		result.put("PID", System.getProperty("PID"));
		result.put("ObjectPendingFinalizationCount",
				ManagementFactory.getMemoryMXBean().getObjectPendingFinalizationCount());
		// result.put("ObjectName",
		// ManagementFactory.getMemoryMXBean().getObjectName());
		result.put("LoadedClassCount", ManagementFactory.getClassLoadingMXBean().getLoadedClassCount());
		result.put("TotalLoadedClassCount", ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount());
		result.put("TotalCompilationTime", ManagementFactory.getCompilationMXBean().getTotalCompilationTime());
		result.put("AvailableProcessors", ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());
		result.put("SystemLoadAverage", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
		Map<String, Object> jvmMemPool = new HashMap<>();
		// 内存池对象
		List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
		for (MemoryPoolMXBean pool : pools) {
			jvmMemPool.put(pool.getName(), new HashMap<String, Object>() {
				private static final long serialVersionUID = 1L;
				{
					put("name", pool.getName());
					put("Type", pool.getType());
					put("ObjectName", pool.getObjectName());
					put("Usage", pool.getUsage().toString());
					put("PeakUsage", pool.getPeakUsage());
					put("CollectionUsage", pool.getCollectionUsage());
				}
			});
		}
		//
		Map<String, Object> garbageCollector = new HashMap<>();
		// gc
		List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();
		for (GarbageCollectorMXBean gc : gcs) {
			garbageCollector.put(gc.getName(), gc);
		}

		return result;
	}
}
