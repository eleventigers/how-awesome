package net.jokubasdargis.awesome.crawler.service;

import com.google.common.collect.ImmutableList;

import net.jokubasdargis.awesome.crawler.CrawlStats;
import net.jokubasdargis.awesome.crawler.Crawler;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.dropwizard.lifecycle.Managed;

import static java.util.Objects.requireNonNull;

final class ManagedCrawler implements Managed {

    public static ManagedCrawler create(CrawlerFactory crawlerFactory,
                                        ScheduledExecutorService executorService) {
        return new ManagedCrawler(crawlerFactory, executorService);
    }


    private final CrawlerFactory crawlerFactory;
    private final ScheduledExecutorService executorService;

    private ManagedCrawler(CrawlerFactory crawlerFactory,
                           ScheduledExecutorService executorService) {
        this.crawlerFactory = requireNonNull(crawlerFactory);
        this.executorService = requireNonNull(executorService);
    }

    @Override
    public void start() throws Exception {
        executorService.scheduleAtFixedRate((Runnable) () -> {
            ScheduledFuture<List<CrawlStats>> job = executorService.schedule(
                    createCrawlTask(), 0, TimeUnit.SECONDS);

            executorService.schedule((Runnable) () -> job.cancel(true), 1, TimeUnit.MINUTES);
        }, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public void stop() throws Exception {

    }

    @SuppressWarnings("WhileLoopReplaceableByForEach")
    private Callable<List<CrawlStats>> createCrawlTask() {
        return () -> {
            Crawler crawler = crawlerFactory.create();
            ImmutableList.Builder<CrawlStats> builder = ImmutableList.builder();
            Iterator<CrawlStats> iterator = crawler.iterator();
            while (iterator.hasNext()) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                CrawlStats stats = iterator.next();
                if (!stats.isSuccess()) {
                    // TODO(eleventigers, 24/03/16): handle crawl fail
                }
                iterator.remove();
                builder.add(stats);
            }
            return builder.build();
        };
    }
}
