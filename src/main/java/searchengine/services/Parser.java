package searchengine.services;

import searchengine.model.Page;

public interface Parser {
	
	Page parse(String url);
}
