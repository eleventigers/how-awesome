package net.jokubasdargis.awesome.crawler.service;

import net.jokubasdargis.awesome.crawler.Crawler;

interface CrawlerFactory {
    Crawler create();
}
