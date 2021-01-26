package nuaa.dp.hole.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Function;

public class ThreadUtils {
	
	private static final ExecutorService executor = Executors.newCachedThreadPool();
	
	public static <F, T> void newThread(T arg, Function<? super T, F> function){
		executor.execute(new InnerThread<F, T>(arg, function));
	}
	
	static class InnerThread<F, T> extends Thread{
		final T argValue;
		final Function<? super T, ? extends F> function;
		public InnerThread(T arg, Function<? super T, ? extends F> function){
			this.argValue = arg;
			this.function = function;
		}
		
		@Override
		public void run(){
			this.function.apply(this.argValue);
		}
	}

	public static void sleep(long ms){
		if(ms<0){
			return;
		}
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}
}
