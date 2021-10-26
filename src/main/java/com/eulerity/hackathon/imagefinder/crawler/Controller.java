package com.eulerity.hackathon.imagefinder.crawler;

import com.eulerity.hackathon.imagefinder.utils.ImageUrlParser;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;


public class Controller {
    public Controller(CopyOnWriteArrayList imageURLs, String url) throws Exception {
        try {
            ImageUrlParser iup = new ImageUrlParser();
            URI formattedURI = iup.formatURL(new URI(url));
            if (formattedURI == null) {
                System.out.println("NULL URI");
                return;
            }

            url = formattedURI.toString();

            String crawlStorageFolder = "/storage";
            int numberOfCrawlers = 100;

            CrawlConfig config = new CrawlConfig();
            config.setMaxDepthOfCrawling(2);
            config.setPolitenessDelay(50);
            config.setCrawlStorageFolder(crawlStorageFolder);

            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
            RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            robotstxtConfig.setEnabled(false);
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

            controller.addSeed(url);

            // each HTML crawler will share the same CopyWriteArraylist of imageURLS to place their finds.
            CrawlController.WebCrawlerFactory<HTMLCrawler> factory = () -> new HTMLCrawler(imageURLs);

            // Start the crawl. This is a blocking operation, meaning that your code
            // will reach the line after this only when crawling is finished.
            controller.start(factory, numberOfCrawlers);
        } catch (Exception e) {
            System.out.println("Exception from Controller: " + e.getMessage());
        }

    }
}
