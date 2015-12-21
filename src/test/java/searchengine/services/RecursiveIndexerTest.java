package searchengine.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import searchengine.dao.PageDao;
import searchengine.model.Page;
import searchengine.services.Indexer;
import searchengine.services.Parser;
import searchengine.services.RecursiveIndexerImpl;

@RunWith(PowerMockRunner.class)
public class RecursiveIndexerTest {
	
	@Test
	public void testIndexerDepth0() throws IOException, ParseException {
		Parser parser = mock(Parser.class);
		PageDao pageDao = mock(PageDao.class);

		String testUrl1 = "http://test1.com";
		String testUrl2 = "http://test2.com";
		String testUrl3 = "http://test3.com";
		List<String> links = new ArrayList<String>();
		links.add(testUrl2);
		links.add(testUrl3);
		Page rec1 = new Page(testUrl1, "test1", "Test one", links); 
		Page rec2 = new Page(testUrl2, "test2", "Test two", null); 
		Page rec3 = new Page(testUrl3, "test3", "Test three", null); 

		when(parser.parse(testUrl1)).thenReturn(rec1);
		when(parser.parse(testUrl2)).thenReturn(rec2);
		when(parser.parse(testUrl3)).thenReturn(rec3);

		Indexer indexer = new RecursiveIndexerImpl(parser, pageDao);
		indexer.index(testUrl1, 0);
		verify(pageDao, times(1)).add(rec1);
		verify(pageDao, never()).add(rec2);
		verify(pageDao, never()).add(rec3);
	}

	@Test
	public void testIndexerDepth1() throws IOException, ParseException {
		Parser parser = mock(Parser.class);
		PageDao pageDao = mock(PageDao.class);

		String testUrl1 = "http://test1.com";
		String testUrl2 = "http://test2.com";
		String testUrl3 = "http://test3.com";
		List<String> links = new ArrayList<String>();
		links.add(testUrl2);
		links.add(testUrl3);
		Page rec1 = new Page(testUrl1, "test1", "Test one", links); 
		Page rec2 = new Page(testUrl2, "test2", "Test two", null); 
		Page rec3 = new Page(testUrl3, "test3", "Test three", null); 

		when(parser.parse(testUrl1)).thenReturn(rec1);
		when(parser.parse(testUrl2)).thenReturn(rec2);
		when(parser.parse(testUrl3)).thenReturn(rec3);

		Indexer indexer = new RecursiveIndexerImpl(parser, pageDao);
		indexer.index(testUrl1, 1);
		verify(pageDao, times(1)).add(rec1);
		verify(pageDao, times(1)).add(rec2);
		verify(pageDao, times(1)).add(rec3);
	}
	
}
