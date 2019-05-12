package net.jokubasdargis.awesome.aggregator.service;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import net.jokubasdargis.awesome.core.Link;
import net.jokubasdargis.awesome.core.Relationship;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableNode;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

final class LinkRelationshipStore {

    private final Link root;
    private final MutableGraph<Link> graph = GraphBuilder.directed().allowsSelfLoops(true).build();

    LinkRelationshipStore(Link root) {
        this.root = root;
        graph.addNode(root);
    }

    private static String stringify(Link link) {
        return ((Link.Identified) link).canonicalize();
    }

    boolean put(Relationship<Link> relationship) {
        synchronized (graph) {
            return graph.putEdge(relationship.from(), relationship.to());
        }
    }

    File dumpGraphToFile() {
        return dumpGraphToFile(Format.SVG).get(0);
    }

    List<File> dumpGraphToFile(Format... formats) {
        try {
            String rootNodeName = stringify(root);
            guru.nidi.graphviz.model.MutableGraph
                    renderGraph = mutGraph(rootNodeName).setDirected(true);


            Map<String, MutableNode> nodeMap = new HashMap<>();
            Function<String, MutableNode> createAndStoreMutableNode = s -> {
                MutableNode node = mutNode(s);
                renderGraph.add(node);
                return node;
            };
            nodeMap.computeIfAbsent(rootNodeName, createAndStoreMutableNode);

            ImmutableGraph<Link> immutableGraph;
            synchronized (graph) {
                immutableGraph = ImmutableGraph.copyOf(graph);
            }

            for (EndpointPair<Link> links : immutableGraph.edges()) {
                if (links.source() instanceof Link.Identified) {
                    MutableNode sourceNode = nodeMap
                            .computeIfAbsent(stringify(links.source()), createAndStoreMutableNode);
                    MutableNode targetNode = nodeMap
                            .computeIfAbsent(stringify(links.target()), createAndStoreMutableNode);
                    if (!sourceNode.name().equals(targetNode.name())) {
                        sourceNode.addLink(targetNode);
                    }
                }
            }

            Graphviz graphviz = Graphviz.fromGraph(renderGraph);
            List<File> files = new ArrayList<>();
            for (Format format : formats) {
                Path tmpPath = Files.createTempFile("graph", null);
                File file = new File("/home/eleventigers/Downloads", "graphviz." + format.name().toLowerCase());
                graphviz.render(format).toFile(tmpPath.toFile());
                Files.copy(tmpPath, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                files.add(file);
            }

            return files;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
