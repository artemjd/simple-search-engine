package searchengine.services;

import java.util.List;

import searchengine.dao.PageDao;
import searchengine.model.Page;

public class AppServiceImpl implements AppService {

	private Indexer indexer;
	private PageDao pageDao;
	
	public AppServiceImpl(Indexer indexer, PageDao pageDao) {
		this.indexer = indexer;
		this.pageDao = pageDao;
	}

	public List<Page> getPages(String query) {
		return pageDao.get(query);
	}

	public List<String> indexUrl(String url, int depth) {
		return indexer.index(url, depth);
	}
	
}
