package searchengine.services;

import java.util.List;

import searchengine.model.Page;

public interface AppService {

	List<Page> getPages(String searchQuery);
	List<String> indexUrl(String url, int depth);
}
