package searchengine.dao;

import java.util.List;

import searchengine.model.Page;

public interface PageDao {
	void add(Page page);
	void add(List<Page> pages);
	List<Page> get(String query);

}
