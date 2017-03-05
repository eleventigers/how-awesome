package net.jokubasdargis.awesome.aggregator.service;

import com.google.common.collect.ImmutableList;

import net.jokubasdargis.awesome.core.Link;
import net.jokubasdargis.awesome.core.LinkDefinition;
import net.jokubasdargis.awesome.core.LinkOccurrence;
import net.jokubasdargis.awesome.message.MessageParcel;
import net.jokubasdargis.awesome.message.MessageQueue;
import net.jokubasdargis.awesome.message.MessageRouter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.dropwizard.lifecycle.Managed;

import static java.util.Objects.requireNonNull;

final class ManagedMessageLogger implements Managed {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedMessageLogger.class);

    static ManagedMessageLogger create(
            MessageRouter messageRouter, ScheduledExecutorService executorService) {
        return new ManagedMessageLogger(messageRouter, executorService);
    }

    private final MessageRouter messageRouter;
    private final ScheduledExecutorService executorService;

    private ManagedMessageLogger(MessageRouter messageRouter,
                                 ScheduledExecutorService executorService) {
        this.messageRouter = requireNonNull(messageRouter);
        this.executorService = requireNonNull(executorService);
    }

    @Override
    public void start() throws Exception {
        executorService.scheduleAtFixedRate(() -> {
            createLogTasks(messageRouter, LOGGER)
                    .stream()
                    .map((Function<Runnable, ScheduledFuture<?>>) runnable ->
                            executorService.schedule(runnable, 0, TimeUnit.SECONDS))
                    .forEach(scheduledFuture ->
                            executorService.schedule(
                                    () -> scheduledFuture.cancel(true), 10, TimeUnit.SECONDS));
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void stop() throws Exception {

    }

    private static <T> Runnable createLogTask(MessageQueue<T> queue, Logger logger) {
        return () -> {
            while (!Thread.currentThread().isInterrupted()){
                MessageParcel<T> parcel = queue.peek();
                if (parcel != null) {
                    logger.info(parcel.toString());
                    queue.remove();
                }
            }
        };
    }

    private static List<Runnable> createLogTasks(MessageRouter messageRouter, Logger logger) {
        ImmutableList.Builder<Runnable> builder = ImmutableList.builder();
        builder.add(createLogTask(queueFrom(
                messageRouter, LinkOccurrence.class), logger));
        builder.add(createLogTask(queueFrom(
                messageRouter, LinkDefinition.Title.class), logger));
        builder.add(createLogTask(queueFrom(
                messageRouter, LinkDefinition.Description.class), logger));
        builder.add(createLogTask(queueFrom(
                messageRouter, LinkDefinition.Relationship.class), logger));
        builder.add(createLogTask(queueFrom(
                messageRouter, LinkDefinition.ForksCount.class), logger));
        builder.add(createLogTask(queueFrom(
                messageRouter, LinkDefinition.StarsCount.class), logger));
        builder.add(createLogTask(queueFrom(
                messageRouter, LinkDefinition.LatestCommitDate.class), logger));
        return builder.build();
    }

    private static <T> MessageQueue<T> queueFrom(MessageRouter messageRouter, Class<T> klass) {
        return messageRouter.route(kotlin.jvm.JvmClassMappingKt.getKotlinClass(klass));
    }
}
