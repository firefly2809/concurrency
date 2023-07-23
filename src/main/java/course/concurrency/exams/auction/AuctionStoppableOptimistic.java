package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicMarkableReference<Bid> atomicMarkableReferenceBid = new AtomicMarkableReference<>(
            new Bid(null, null, 0L),
            false // флаг остановки аукциона и того, что ставка победила
    );

    public boolean propose(Bid bid) {
        Bid actualLatestBid;
        boolean[] auctionIsStoppedMarkHolder = new boolean[1];
        do {
            actualLatestBid = atomicMarkableReferenceBid.get(auctionIsStoppedMarkHolder);
            if (bid.getPrice() <= actualLatestBid.getPrice() || auctionIsStoppedMarkHolder[0])
                return false;
        } while (!atomicMarkableReferenceBid.compareAndSet(
                actualLatestBid,
                bid,
                false,
                false)
        );
        notifier.sendOutdatedMessage(actualLatestBid);
        return true;
    }

    public Bid getLatestBid() {
        return atomicMarkableReferenceBid.getReference();
    }

    public Bid stopAuction() {
        Bid actualLatestBid;
        do {
            actualLatestBid = atomicMarkableReferenceBid.getReference();
        } while (!atomicMarkableReferenceBid.attemptMark(actualLatestBid, true));
        return actualLatestBid;
    }
}
