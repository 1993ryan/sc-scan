package nuaa.dp.hole.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.*;

/**
 * @Copyright ZhengRen
 * @Description:
 * @version: v1.0.0
 * @author: Dapeng Yan
 * @date: 2020/12/11 11:11
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ---------------------------------------------------------*
 * 2020/12/11      Dapeng Yan          v1.0.0
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

    private ExecutorService taskExecutor = new ThreadPoolExecutor(8, 32, 60, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(1024), new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        //当然了，这里设置的线程池是corePoolSize也是很关键了，自己根据业务需求设定
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(16));

        /**为什么这么说呢？
         假设你有4个任务需要每隔1秒执行，而其中三个都是比较耗时的操作可能需要10多秒，而你上面的语句是这样写的：
         taskRegistrar.setScheduler(Executors.newScheduledThreadPool(3));
         那么仍然可能导致最后一个任务被阻塞不能定时执行
         **/
    }
}