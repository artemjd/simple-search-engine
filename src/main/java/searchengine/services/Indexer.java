package searchengine.services;

import java.util.List;

public interface Indexer {
	
	List<String> index(String url, int depth);
}
