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
        if (stopped)
            return false;
        Bid actualLatestBid;
        do {
            actualLatestBid = atomicLatestBid.get();
            if (bid.getPrice() <= actualLatestBid.getPrice())
                return false;
        } while (!atomicLatestBid.compareAndSet(actualLatestBid, bid));
        notifier.sendOutdatedMessage(actualLatestBid);
        return true;
    }

    public Bid getLatestBid() {
        return atomicLatestBid.get();
    }

    public Bid stopAuction() {
        stopped = true;
        return atomicLatestBid.get();
    }
}
