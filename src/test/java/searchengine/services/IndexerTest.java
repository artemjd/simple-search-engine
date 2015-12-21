package searchengine.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import searchengine.dao.PageDao;
import searchengine.model.Page;
import searchengine.services.Indexer;
import searchengine.services.IndexerImpl;
import searchengine.services.Parser;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
public class IndexerTest {
	
	@Test
	public void testIndexerDepth0() throws IOException, ParseException {
		Parser parser = mock(Parser.class);
		PageDao pageDao = mock(PageDao.class);

		String url1 = "http://test1.com";
		String url2 = "http://test2.com";
		String url3 = "http://test3.com";
		List<String> links = new ArrayList<String>(Arrays.asList(url2, url3));
		Page rec1 = new Page(url1, "test1", "Test one", links); 
		Page rec2 = new Page(url2, "test2", "Test two", null); 
		Page rec3 = new Page(url3, "test3", "Test three", null); 
		
		when(parser.parse(url1)).thenReturn(rec1);
		when(parser.parse(url2)).thenReturn(rec2);
		when(parser.parse(url3)).thenReturn(rec3);

		Indexer indexer = new IndexerImpl(parser, pageDao, 3);
		List<String> urls = indexer.index(url1, 0);
		assertEquals(1, urls.size());
	}

	@Test
	public void testIndexerDepth1() throws IOException, ParseException {
		Parser parser = mock(Parser.class);
		PageDao pageDao = mock(PageDao.class);

		String url1 = "http://test1.com";
		String url2 = "http://test2.com";
		String url3 = "http://test3.com";
		String url4 = "http://test4.com";
		String url5 = "http://test5.com";
		List<String> url1links = new ArrayList<String>(Arrays.asList(url2, url3, url4));
		List<String> url2links = new ArrayList<String>(Arrays.asList(url5));
		Page rec1 = new Page(url1, "test1", "Test one", url1links); 
		Page rec2 = new Page(url2, "test2", "Test two", url2links); 
		Page rec3 = new Page(url3, "test3", "Test thress", null); 
		Page rec4 = new Page(url4, "test4", "Test four", null); 
		Page rec5 = new Page(url5, "test5", "Test five", null); 

		when(parser.parse(url1)).thenReturn(rec1);
		when(parser.parse(url2)).thenReturn(rec2);
		when(parser.parse(url3)).thenReturn(rec3);
		when(parser.parse(url4)).thenReturn(rec4);
		when(parser.parse(url5)).thenReturn(rec5);

		Indexer indexer = new IndexerImpl(parser, pageDao, 3);
		List<String> urls = indexer.index(url1, 1);
		assertEquals(4, urls.size());
		assertTrue(urls.contains(url1));
		assertTrue(urls.contains(url2));
		assertTrue(urls.contains(url3));
		assertTrue(urls.contains(url4));

	}
	
}
