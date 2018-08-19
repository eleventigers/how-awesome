package net.jokubasdargis.awesome.aggregator.service;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import net.jokubasdargis.awesome.core.Link;
import net.jokubasdargis.awesome.core.LinkDefinition;
import net.jokubasdargis.awesome.core.LinkOccurrence;
import net.jokubasdargis.awesome.core.Relationship;
import net.jokubasdargis.awesome.message.MessageParcel;
import net.jokubasdargis.awesome.message.MessageQueue;
import net.jokubasdargis.awesome.message.MessageRouter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import io.dropwizard.lifecycle.Managed;

final class ManagedMessageLogger implements Managed {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedMessageLogger.class);

    static ManagedMessageLogger create(
            MessageRouter messageRouter,
            LinkRelationshipStore linkRelationshipStore,
            ScheduledExecutorService executorService) {
        return new ManagedMessageLogger(messageRouter, linkRelationshipStore, executorService);
    }

    private final MessageRouter messageRouter;
    private final LinkRelationshipStore linkRelationshipStore;
    private final ScheduledExecutorService executorService;

    @Nullable
    private ScheduledFuture<?> scheduledTask;

    private ManagedMessageLogger(
            MessageRouter messageRouter,
            LinkRelationshipStore linkRelationshipStore,
            ScheduledExecutorService executorService) {
        this.messageRouter = requireNonNull(messageRouter);
        this.linkRelationshipStore = linkRelationshipStore;
        this.executorService = requireNonNull(executorService);
    }

    @Override
    public void start() throws Exception {
        List<Runnable> tasks = createTasks(messageRouter, linkRelationshipStore, LOGGER);
        scheduledTask = executorService.scheduleAtFixedRate(() -> {
            tasks.stream()
                    .map((Function<Runnable, ScheduledFuture<?>>) runnable ->
                            executorService.schedule(runnable, 0, TimeUnit.SECONDS))
                    .forEach(scheduledFuture ->
                            executorService.schedule(
                                    () -> scheduledFuture.cancel(true), 25, TimeUnit.SECONDS));
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public void stop() throws Exception {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            scheduledTask = null;
        }
    }

    private static <T> Runnable createLogTask(MessageQueue<T> queue, Logger logger) {
        return createLogTask(queue, logger, true);
    }

    private static <T> Runnable createLogTask(
            MessageQueue<T> queue, Logger logger, boolean removeOnRead) {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                MessageParcel<T> parcel = queue.peek();
                if (parcel != null) {
                    logger.info(parcel.toString());
                    if (removeOnRead) {
                        queue.remove();
                    }
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

    private static List<Runnable> createGraphTasks(
            MessageRouter messageRouter,
            LinkRelationshipStore linkRelationshipStore, Logger logger) {
        ImmutableList.Builder<Runnable> builder = ImmutableList.builder();
        MessageQueue<LinkDefinition.Relationship> queue = queueFrom(
                messageRouter, LinkDefinition.Relationship.class);
        builder.add(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                MessageParcel<LinkDefinition.Relationship> parcel = queue.peek();
                if (parcel != null) {
                    Relationship<Link> relationship = parcel.getValue();
                    if (linkRelationshipStore.put(relationship)) {
                        // TODO: handle insertion failure
                    }
                    queue.remove();
                }
            }
        });
        return builder.build();
    }

    private static Set<Link> getSuccessors(Graph<Link> graph, Link node) {
        Set<Link> successors = new HashSet<>();
        for (Link successor : graph.successors(node)) {
            successors.addAll(graph.successors(successor));
        }
        successors.remove(node);
        return successors;
    }

    private static List<Runnable> createTasks(
            MessageRouter messageRouter,
            LinkRelationshipStore linkRelationshipStore, Logger logger) {
        List<Runnable> logTasks = createLogTasks(messageRouter, logger);
        List<Runnable> graphTasks = createGraphTasks(messageRouter, linkRelationshipStore, logger);
        return ImmutableList.<Runnable>builder()
//                .addAll(logTasks)
                .addAll(graphTasks)
                .build();
    }

    private static <T> MessageQueue<T> queueFrom(MessageRouter messageRouter, Class<T> klass) {
        return messageRouter.route(kotlin.jvm.JvmClassMappingKt.getKotlinClass(klass));
    }
}
