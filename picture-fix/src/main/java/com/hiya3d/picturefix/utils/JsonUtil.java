package com.hiya3d.picturefix.utils;

import java.util.LinkedList;
import java.util.Queue;

import com.alibaba.fastjson.JSONObject;

/**
 * json解析工具
 * @author Rex.Tan
 * @date 2021-12-9 13:49:13
 */
public class JsonUtil {

	/**
	 * 递归取值, String lab = metadata.status.label
	 * @author Rex.Tan
	 * @date 2021-12-9 14:08:26
	 * @param json
	 * @param field
	 * @return
	 */
	public static String getField(JSONObject json, String field) {
		if(json == null || field == null) {
			return null;
		}
		String[] array = field.split(".");
		Queue<String> queue = new LinkedList<String>();
		for(String item: array) {
			queue.offer(item);
		}
		if(queue.size() == 0) {
			return null;
		}
		Object val = getField(json, queue);
		
		return String.valueOf(val);
	}
	
	private static Object getField(JSONObject json, Queue<String> queue) {
		String key = queue.poll();
		Object val = null;
		if(json.containsKey(key)) {
			val = json.get(key);
			if(val instanceof JSONObject && queue.size() > 0) {
				JSONObject obj = JSONObject.parseObject(val.toString());
				val = getField(obj, queue);
			}
		}
		return val;
	}
	
}
