package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }


    private final AtomicReference<Bid> atomicLatestBid = new AtomicReference<>(new Bid(null, null, 0L));

    public boolean propose(Bid bid) {
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
}
