package com.eulerity.hackathon.imagefinder.crawler;

import com.eulerity.hackathon.imagefinder.utils.ImageUrlParser;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

public class HTMLCrawler extends WebCrawler{
    private CopyOnWriteArrayList<String> imageURLs;

    public HTMLCrawler(CopyOnWriteArrayList imageURLs) {
        this.imageURLs = imageURLs;
    }

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && href.startsWith("https://www.rutgers.edu/");
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            ImageUrlParser iup = new ImageUrlParser();
            List<String> pageImageURLs = iup.getImageUrlsOfPage(url);
            if (pageImageURLs != null && pageImageURLs.size() > 0) {
                Runnable addToImageURLsList = () -> {
                    for (String imageURL : pageImageURLs) {
                        System.out.println("Adding imageURL: " + imageURL);
                        imageURLs.add(imageURL);
                    }
                };

                setThread(new Thread(addToImageURLsList));
                getThread().start();
            }
        }
    }
}
