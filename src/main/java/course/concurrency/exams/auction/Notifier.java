package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notifier {

    private final ExecutorService notificationExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public void sendOutdatedMessage(Bid bid) {
        CompletableFuture.runAsync(this::imitateSending, notificationExecutor);
    }

    private void imitateSending() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }

    public void shutdown() {
        notificationExecutor.shutdown();
    }
}
