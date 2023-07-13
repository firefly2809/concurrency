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
        boolean bidChanged = false;
        if (bid.getPrice() > atomicLatestBid.get().getPrice()) {
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
}
