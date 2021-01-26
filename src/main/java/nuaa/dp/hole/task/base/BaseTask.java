package nuaa.dp.hole.task.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class BaseTask implements Runnable {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void run() {
		try {
			this.doInternalTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract void doInternalTask() throws Exception;

	public static String getHostName(){
		try {
			InetAddress ia = InetAddress.getLocalHost();
			return ia.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}
}
