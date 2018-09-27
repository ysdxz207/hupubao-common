package win.hupubao.common.scheduler;

import java.util.concurrent.*;

/**
 *
 * @author Moses.wei
 * @date 2018-03-24 13:04:13
 * 任务调度器
 */
public class Scheduler {
    private static final ScheduledExecutorService SVC = Executors
            .newSingleThreadScheduledExecutor();

    public static ConcurrentMap<String,ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    public static void runTimer(String schedualerName,
                                long delay,
                                long interval,
                                Runnable runnable) {

        futures.put(schedualerName , SVC.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.MILLISECONDS));
    }

    public static void runOnce(String schedualerName,
                                long delay,
                                Runnable runnable) {

        futures.put(schedualerName , SVC.schedule(runnable, delay, TimeUnit.MILLISECONDS));
    }

    public static void cancel(String schedualerName) {
        futures.get(schedualerName).cancel(true);
        futures.remove(schedualerName);
    }

}
