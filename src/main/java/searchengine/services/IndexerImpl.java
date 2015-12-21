package searchengine.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.util.StopWatch;

import searchengine.dao.PageDao;
import searchengine.exception.AppException;
import searchengine.model.Page;

public class IndexerImpl implements Indexer {

	private static final Logger logger = Logger.getLogger("indexer");
	private static final int PAGE_BUFFER_MAX_SIZE = 100;
	private PageDao pageDao;
	private Parser parser;
	private int maxDepth;
	private int taskCount;
	private List<Page> pageBuffer = new ArrayList<>();
	private Set<String> indexedUrls = new HashSet<>();
	private CompletionService<ParserTask> completionService;
	private ExecutorService execService;

	public IndexerImpl(Parser parser, PageDao pageDao, int threadCount) {
		this.parser = parser;
		this.pageDao = pageDao;
		execService = Executors.newFixedThreadPool(threadCount);
		completionService = new ExecutorCompletionService<>(execService);
	}

	public List<String> index(String url, int depth) {
		logger.info("Base url: " + url + " depth: " + depth);
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		maxDepth = depth;
		taskCount = 0;
		submitNewTask(url, 0);
		while (taskCount > 0) {
			try {
				Future<ParserTask> future = completionService.take();
				taskCount--;
				pageBuffer.add(future.get().getPage());
				processResult(future.get());
				logger.debug("Task count = " + taskCount);
			} catch (InterruptedException | ExecutionException e) {
				throw new AppException(e);
			}
			
			if (pageBuffer.size() == PAGE_BUFFER_MAX_SIZE){
				pageDao.add(pageBuffer);
				pageBuffer.clear();
			}

		}
		pageDao.add(pageBuffer);
		pageBuffer.clear();

		stopWatch.stop();
		logger.info("URLs indexed:" + indexedUrls.size());
		logger.info("Task time:" + stopWatch.getTotalTimeSeconds());
		List<String> urls = new ArrayList<>(indexedUrls);
		indexedUrls.clear();
		return new ArrayList<>(urls);

	}

	private void processResult(ParserTask task) {
		if (task.getDepth() == maxDepth || task.getPage() == null
				|| task.getPage().getLinks() == null)
			return;

		for (String link : task.getPage().getLinks()) {
			submitNewTask(link, task.getDepth() + 1);
		}
	}

	private void submitNewTask(String url, int depth) {
		if (indexedUrls.add(url)) {
			completionService.submit(new ParserTask(parser, url, depth));
			taskCount++;
			logger.debug("Task submitted: " + " depth = " + depth + " url = "
					+ url);
		}
	}
	
	@PreDestroy
	public void destroy(){
		execService.shutdown();
	}

	private static class ParserTask implements Callable<ParserTask> {

		private Parser parser;

		private String url;

		private int depth;

		private Page page;

		public ParserTask(Parser parser, String url, int depth) {
			super();
			this.parser = parser;
			this.url = url;
			this.depth = depth;
		}

		@Override
		public ParserTask call() throws Exception {
			page = parser.parse(url);
			return this;
		}

		public Page getPage() {
			return page;
		}

		public int getDepth() {
			return depth;
		}

	}
}
