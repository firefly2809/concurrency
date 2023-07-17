package course.concurrency.exams.auction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notifier {

    private final ExecutorService notificationExecutor = Executors.newFixedThreadPool(25);

    public void sendOutdatedMessage(Bid bid) {
        notificationExecutor.submit(this::imitateSending);
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
