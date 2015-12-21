package searchengine.dao;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import searchengine.exception.AppException;
import searchengine.model.Page;

public class LucenePageDao implements PageDao {

	private static final String URL = "url";
	private static final String TITLE = "title";
	private static final String TEXT = "text";
	private static final Logger logger = Logger.getLogger(LucenePageDao.class);
	private static final int HIT_COUNT = 500;
	private IndexWriter writer;
	private QueryParser parser;
	private Analyzer analyzer;
	private String indexDirectory;

	public LucenePageDao(String indexDirectory, Analyzer analyzer) {
		this.indexDirectory = indexDirectory;
		this.analyzer = analyzer;
	}

	@PostConstruct
	public void init() {
		parser = new QueryParser(TEXT, analyzer);

		try {
			writer = new IndexWriter(
					FSDirectory.open(Paths.get(indexDirectory)),
					new IndexWriterConfig(analyzer));
		} catch (IOException e) {
			logger.error("Couldn't open index directory", e);
			throw new AppException(e);
		}
	}

	@Override
	public void add(List<Page> pages) {
		if (pages == null || pages.isEmpty()) {
			return;
		}
		for (Page page : pages) {
			addPage(page);
		}
		commit();
	}

	@Override
	public void add(Page page) {
		addPage(page);
		commit();
	}

	private void addPage(Page page) {
		if (page == null) {
			return;
		}
		Document doc = new Document();
		doc.add(new StringField(URL, page.getUrl(), Store.YES));
		doc.add(new StringField(TITLE, page.getTitle(), Store.YES));
		doc.add(new TextField(TEXT, page.getText(), Store.NO));

		try {
			writer.updateDocument(new Term(URL, page.getUrl()), doc);
		} catch (IOException e) {
			logger.error("Exception on document update.", e);
			throw new AppException(e);
		}
		logger.debug("Document added: " + page.getUrl());
	}

	private void commit() {
		try {
			writer.commit();
		} catch (IOException e) {
			logger.error("Error during writer commit", e);
			throw new AppException(e);
		}
	}

	@Override
	public List<Page> get(String query) {

		IndexReader reader = null;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths
					.get(indexDirectory)));
		} catch (IOException e) {
			logger.error("Couldn't open index directory", e);
			throw new AppException(e);
		}

		IndexSearcher searcher = new IndexSearcher(reader);
		List<Page> pages = new ArrayList<>();

		try {
			TopDocs results = searcher.search(parser.parse(query), HIT_COUNT);
			ScoreDoc[] hits = results.scoreDocs;
			for (ScoreDoc scoreDoc : hits) {
				Document doc = searcher.doc(scoreDoc.doc);
				pages.add(new Page(doc.get(URL), doc.get(TITLE), null, null));
			}
			reader.close();
		} catch (ParseException e) {
			logger.error("Invalid query", e);
			throw new AppException(e);
		} catch (IOException e) {
			logger.error("Exception while searching", e);
			throw new AppException(e);
		}
		return pages;

	}

	@PreDestroy
	public void close() {
		try {
			writer.close();
		} catch (IOException e) {
			logger.error("Error during writer close", e);
			throw new AppException(e);
		}
	}


}
