package searchengine.config;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import searchengine.dao.LucenePageDao;
import searchengine.dao.PageDao;
import searchengine.services.AppService;
import searchengine.services.AppServiceImpl;
import searchengine.services.Indexer;
import searchengine.services.IndexerImpl;
import searchengine.services.JsoupParser;
import searchengine.services.Parser;

@Configuration
@PropertySource("classpath:app.properties")
public class AppConfig {

	@Value("${app.indexer.directory}")
	String indexDirectory;
	@Value("${app.indexer.threadcount}")
	Integer threadCount;

	@Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    public Parser parser() {
        return new JsoupParser();
    }

    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public Indexer indexer() {
        return new IndexerImpl(parser(), pageDao(), threadCount);
    }

    @Bean
    public AppService appService() {
        return new AppServiceImpl(indexer(), pageDao());
    }

    @Bean
    public PageDao pageDao() {
        return new LucenePageDao(indexDirectory, new StandardAnalyzer());
    }
}
