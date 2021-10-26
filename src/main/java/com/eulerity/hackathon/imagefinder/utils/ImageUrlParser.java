package com.eulerity.hackathon.imagefinder.utils;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ImageUrlParser {
    public List<String> getImageUrlsOfPage(String url) {
        try {
            URI formattedURI = formatURL(new URI(url));
            if (formattedURI == null) {
                return null;
            }

            url = formattedURI.toString();

            Document doc = Jsoup.connect(url).get();
            Elements imageTags = doc.getElementsByTag("img");

            List imageURLs = new ArrayList<String>();
            for (Element image : imageTags) {
                String src = image.attr("src");
                if (src.length() == 0) continue;

                // example: src = /image.png
                src = changeToURLIfRelativePath(src, formattedURI);

                imageURLs.add(src);
            }

            return imageURLs;
        } catch(Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String changeToURLIfRelativePath(String src, URI uri) {
        String url = uri.toString();

        if (src.length() > 2 && src.charAt(0) == '/' && Character.isLetterOrDigit(src.charAt(1))) {
            String uriPath = uri.getPath();

            String pathContents = uriPath.replace("/", "");

            if (pathContents.compareTo("") == 0) {
                src = url + src.substring(1);
            } else {
                int uriPathIndex = url.indexOf(uriPath);
                String uriWithoutPath = url.substring(0, uriPathIndex);

                src = uriWithoutPath + src;
            }
        }

        return src;
    }

    public URI formatURL(URI uri) throws URISyntaxException {
        String url = uri.toString();
        if (uri.getScheme() == null) {
            url = "http://" + url;
        } else if (!uri.getScheme().equals("http")
                || !uri.getScheme().equals("https")) {
            url.replaceFirst(uri.getScheme(), "http");
        }

        uri = new URI(url);

        if (!isValidURL(uri.toString())) {
            return null;
        }

        String domain = uri.getHost();
        String[] domainElements = domain.split("\\.");

        if (domainElements.length != 0
                && !domainElements[0].equals("www")) {
            url = url.replaceFirst(domain, "www." + domain);
        }

        return new URI(url);
    }

    private boolean isValidURL(String url) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }
}
