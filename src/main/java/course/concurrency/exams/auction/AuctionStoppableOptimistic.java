package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> atomicLatestBid = new AtomicReference<>(new Bid(null, null, 0L));

    private volatile boolean stopped;

    public boolean propose(Bid bid) {
        Bid latestBid = atomicLatestBid.get();
        boolean bidChanged = false;
        if (bid.getPrice() > atomicLatestBid.get().getPrice() && !stopped) {
            // если ставка успешно изменилась или вдруг стала уже неактуальной выходим из цикла
            while (!bidChanged && bid.getPrice() > atomicLatestBid.get().getPrice())
                bidChanged = atomicLatestBid.compareAndSet(atomicLatestBid.get(), bid);
        }

        if (bidChanged)
            notifier.sendOutdatedMessage(latestBid);

        return bidChanged;
    }

    public Bid getLatestBid() {
        return atomicLatestBid.get();
    }

    public Bid stopAuction() {
        stopped = true;
        return atomicLatestBid.get();
    }
}
