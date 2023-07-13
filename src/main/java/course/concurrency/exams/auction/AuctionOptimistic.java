package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }


    private final AtomicReference<Bid> atomicLatestBid = new AtomicReference<>(new Bid(null, null, 0L));

    public boolean propose(Bid bid) {
        Bid latestBid = atomicLatestBid.get();
        if (bid.getPrice() > atomicLatestBid.get().getPrice()) {
            if (atomicLatestBid.compareAndSet(atomicLatestBid.get(), bid)) {
                notifier.sendOutdatedMessage(latestBid);
                return true;
            } else
                propose(bid);
        }
        return false;
    }

    public Bid getLatestBid() {
        return atomicLatestBid.get();
    }
}
