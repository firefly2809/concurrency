package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(null, null, 0L);
    private volatile boolean stopped;

    public boolean propose(Bid bid) {
        if (bid.getPrice() < latestBid.getPrice() || stopped)
            return false; // сразу отдаем false если ставка меньше последней или аукцион остановлен
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

    public Bid stopAuction() {
        stopped = true;
        return latestBid;
    }
}
