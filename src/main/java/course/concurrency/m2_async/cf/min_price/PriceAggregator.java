package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    /*
     * C ForkJoinPool.commonPool() тесты падают.
     * Добавил cached, так как неизвестно сколько магазинов будет на входе, так как нагрузка в основновном блокирующая
     */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        Double result = Double.NaN;
        NavigableSet<Double> minPriceSorted = new ConcurrentSkipListSet<>();

        shopIds.forEach(shopId ->
                CompletableFuture
                        .runAsync(
                                () -> minPriceSorted.add(priceRetriever.getPrice(itemId, shopId)),
                                executorService
                        )
        );

        try {
            CompletableFuture
                    .runAsync(() -> {
                        while (minPriceSorted.size() < shopIds.size()) {
                            //просто ждем, когда получим цены со всех магазинов
                        }
                    })
                    .get(2950, TimeUnit.MILLISECONDS);
            result = minPriceSorted.first();
        } catch (TimeoutException e) {
            if (!minPriceSorted.isEmpty())
                result = minPriceSorted.first();
        } catch (ExecutionException | InterruptedException e) {
            //
        }

        return result;
    }
}
