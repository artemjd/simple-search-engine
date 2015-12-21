package searchengine.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import searchengine.dao.PageDao;
import searchengine.model.Page;

public class RecursiveIndexerImpl implements Indexer {

	private static final Logger logger = Logger.getLogger("indexer");
	private PageDao pageDao;
	private Parser parser;
	private List<String> visitedUrls = new ArrayList<>();

	public RecursiveIndexerImpl(Parser parser, PageDao pageDao) {
		this.parser = parser;
		this.pageDao = pageDao;
	}

	public List<String> index(String url, int depth) {
		logger.info("Indexing started... Base url: " + url + "; depth: " + depth);
		Map<String, Integer> indexedLinks = new HashMap<>();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		indexRecursively(url, depth, indexedLinks);
		stopWatch.stop();
		logger.info("Number of urls indexed: " + indexedLinks.size());
		logger.info("Number of urls visited:" + visitedUrls.size());
		logger.info("Task time:" + stopWatch.getTotalTimeSeconds());
		List<String> urls = new ArrayList<>(indexedLinks.keySet());
		visitedUrls.clear();
		indexedLinks.clear();
		return urls;
	}

	private void indexRecursively(String url, int depth,
			Map<String, Integer> indexedLinks) {
		visitedUrls.add(url);
		depth--;

		// process URL only if it was not processed on higher level
		Integer previousUrlDepth = indexedLinks.get(url);
		if (previousUrlDepth != null && previousUrlDepth >= depth) {
			return;
		} else {
			indexedLinks.put(url, depth);
		}

		Page parseRes = parser.parse(url);
		pageDao.add(parseRes);

		if (depth < 0 || parseRes == null || parseRes.getLinks() == null
				|| parseRes.getLinks().isEmpty())
			return;

		for (String link : parseRes.getLinks()) {
			indexRecursively(link, depth, indexedLinks);
		}

	}

}
