package course.concurrency.exams.auction;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    // сделал volatile, чтобы не синхронизировать метод get
    private volatile Bid latestBid = new Bid(null, null, 0L);

    public boolean propose(Bid bid) {
        if (bid.getPrice() < latestBid.getPrice())
            return false; // сразу отдаем false если ставка меньше последней
        synchronized (this) {
            if (bid.getPrice() > latestBid.getPrice()) { // еще раз чекаем, что ставку надо поменять
                notifier.sendOutdatedMessage(latestBid);
                latestBid = bid;
                return true;
            }
            return false;
        }
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
