package searchengine.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;

import searchengine.model.Page;
import searchengine.services.Parser;

public class JsoupParser implements Parser {

	private static final Logger logger = Logger.getLogger(JsoupParser.class);
	private final static String HREF_QUERY = "a[href]";
	private final static String HREF_ATTR = "href";

	@Value("${jsoup.user_agent}")
	private String userAgent;
	@Value("${jsoup.timeout}")
	private int timeout;
	@Value("${jsoup.max_body_size}")
	private int maxBodySize;
	@Value("${jsoup.header_name}")
	private String headerName;
	@Value("${jsoup.header_value}")
	private String headerValue;
	
	public Page parse(String url) {

		Document doc = null;
		try {
			doc = Jsoup.connect(url).header(headerName, headerValue)
					.userAgent(userAgent).maxBodySize(maxBodySize)
					.timeout(timeout).get();

			logger.trace("Connected to url : " + url);
		} catch (IOException | IllegalArgumentException e) {
			logger.trace("Coudn't connect to url : " + url);
			return null;
		}

		Page page = new Page(url, doc.title(), doc.text(),
				parseHrefs(url, doc.select(HREF_QUERY)));
		return page;

	}

	private List<String> parseHrefs(String url, Elements elements) {
		List<String> hrefs = new ArrayList<>();
		for (Element el : elements) {
			String href = toAbsoluteUrl(url, el.attr(HREF_ATTR));
			if (href != null) {
				hrefs.add(href);
			}
		}
		return hrefs;
	}

	private String toAbsoluteUrl(String url, String href) {
		if (href.startsWith("#"))
			return null;

		URL absUrl = null;
		try {
			absUrl = new URL(new URL(url), href);
		} catch (MalformedURLException e) {
			logger.trace("Invalid link: " + href);
			return null;
		}
		return absUrl.toString();

	}
	
}
