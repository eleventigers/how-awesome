package net.jokubasdargis.awesome.aggregator.service;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.util.HashSet;
import java.util.Set;

import net.jokubasdargis.awesome.core.Link;
import net.jokubasdargis.awesome.core.Relationship;

final class LinkRelationshipStore {

    private final Link root;
    private final MutableGraph<Link> graph = GraphBuilder.directed().allowsSelfLoops(true).build();

    LinkRelationshipStore(Link root) {
        this.root = root;
        graph.addNode(root);
    }

    boolean put(Relationship<Link> relationship) {
        return graph.putEdge(relationship.from(), relationship.to());
    }

    private static Set<Link> getSuccessors(Graph<Link> graph, Link node) {
        Set<Link> successors = new HashSet<>();
        successors.addAll(graph.successors(node));
        successors.remove(node);
        return successors;
    }
}
