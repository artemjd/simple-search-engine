package searchengine.services;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import searchengine.dao.LucenePageDao;
import searchengine.model.Page;

public class LucenePageDaoTest {

	private static final String INDEX_DIRECTORY = "index_test";

	@After
	public void after() throws IOException {
		deleteAll();
	}

	@BeforeClass
	public static void beforeClass() throws IOException {
		FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		deleteAll();
	}

	public static void deleteAll() throws IOException {
		IndexWriter writer = new IndexWriter(FSDirectory.open(Paths
				.get(INDEX_DIRECTORY)), new IndexWriterConfig(
				new StandardAnalyzer()));
		writer.deleteAll();
		writer.close();
	}

	@Test
	public void searchSinglePage() throws IOException, ParseException {
		LucenePageDao pageDao = new LucenePageDao(INDEX_DIRECTORY,
				new StandardAnalyzer());
		pageDao.init();
		Page testPage = new Page("test_url", "test_title", "Example text", null);
		pageDao.add(testPage);
		pageDao.close();

		List<Page> pages = pageDao.get("example");
		assertEquals(1, pages.size());
		assertEquals(testPage.getUrl(), pages.get(0).getUrl());
		assertEquals(testPage.getTitle(), pages.get(0).getTitle());

	}

	@Test
	public void searchByText() throws IOException, ParseException {
		LucenePageDao pageDao = new LucenePageDao(INDEX_DIRECTORY,
				new StandardAnalyzer());
		pageDao.init();
		Page page1 = new Page("test_url", "test_title", "Example text", null);
		Page page2 = new Page("test_url2", "test_title2", "Example text two",
				null);
		List<Page> pages = new ArrayList<Page>(Arrays.asList(page1, page2));
		pageDao.add(pages);
		pageDao.close();

		pages = pageDao.get("example text");
		assertEquals(2, pages.size());
		assertTrue(pages.contains(page1));
		assertTrue(pages.contains(page2));

		pages = pageDao.get("two");
		assertEquals(1, pages.size());
		assertTrue(pages.contains(page2));

	}

	@Test
	public void searchByMissingText() throws IOException, ParseException {
		LucenePageDao pageDao = new LucenePageDao(INDEX_DIRECTORY,
				new StandardAnalyzer());
		pageDao.init();
		Page page1 = new Page("test_url", "test_title", "Example text", null);
		Page page2 = new Page("test_url2", "test_title2", "Example text two",
				null);
		List<Page> pages = new ArrayList<Page>(Arrays.asList(page1, page2));
		pageDao.add(pages);
		pageDao.close();

		pages = pageDao.get("TESTTEST");
		assertNotNull(pages);
		assertTrue(pages.isEmpty());
	}

}
