package nuaa.dp.hole.task.base;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public enum TaskTopic {
	REPOSITORY_CLONE(101, 60 * 60),
	COMMIT_HISTORY_MATCH(102, 10 * 60),
	FETCH_POM_CONTENT(103, 5 * 60),
	;

	/** TOPIC ID */
	private int topic;

	/** 任务执行的时间间隔，单位秒 */
	private int interval = 300;

	private TaskTopic(int topic, int interval) {
		this.topic = topic;
		this.interval = interval;
	}

	public static TaskTopic valueOf(int topic) {
		for (TaskTopic e : TaskTopic.values()) {
			if (e.topic == topic) {
				return e;
			}
		}
		return null;
	}

	public static Map<Integer, String> toMap() {
		Map<Integer, String> retMap = Maps.newLinkedHashMap();
		List<String> nameList = Lists.newArrayList();
		for (TaskTopic e : TaskTopic.values()) {
			nameList.add(e.name());
		}
		Collections.sort(nameList);
		for(String name: nameList) {
			retMap.put(TaskTopic.valueOf(name).getTopic(), name);
		}
		return retMap;
	}

	public int getTopic() {
		return topic;
	}

	public int getInterval() {
		return interval;
	}

}
