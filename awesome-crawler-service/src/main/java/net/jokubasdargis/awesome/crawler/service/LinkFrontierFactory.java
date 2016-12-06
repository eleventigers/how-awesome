package net.jokubasdargis.awesome.crawler.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.jokubasdargis.awesome.crawler.LinkFrontier;
import net.jokubasdargis.awesome.crawler.LinkFrontiers;

import java.io.File;

import javax.validation.constraints.NotNull;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;

final class LinkFrontierFactory {

    @NotNull
    private File file;

    @JsonProperty
    public File getFile() {
        return file;
    }

    @JsonProperty
    public void setFile(File file) {
        this.file = file;
    }

    LinkFrontier build(Environment environment) {
        LinkFrontier linkFrontier = LinkFrontiers.newFileQueueLinkFrontier(file);
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {

            }

            @Override
            public void stop() throws Exception {
                linkFrontier.close();
            }
        });
        return linkFrontier;
    }
}
