package course.concurrency.m2_async.executors.spring;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringBootAsyncTest {

    @Autowired
    private AsyncClassTest testClass;

    // this method executes after application start
    @EventListener(ApplicationReadyEvent.class)
    public void actionAfterStartup() {
        testClass.runAsyncTask();
        testClass.internalTask();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAsyncTest.class, args);
    }
}
