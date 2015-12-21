package searchengine.services;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import searchengine.model.Page;
import searchengine.services.JsoupParser;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Jsoup.class)
public class JsoupParserTest {

	@Test
	public void parsePage() throws Exception {
		String samplePage = new String(Files.readAllBytes(Paths
				.get(getClass().getResource("/test.html").toURI())));
		Document document = Jsoup.parse(samplePage);
		String testUrl = new String("http://sample.com/sample/sample.html");

		Connection connection = PowerMockito.mock(Connection.class);
		PowerMockito.mockStatic(Jsoup.class);
		PowerMockito.when(Jsoup.connect(testUrl)).thenReturn(connection);

		PowerMockito.when(connection.header(any(), any())).thenReturn(connection);
		PowerMockito.when(connection.userAgent(any())).thenReturn(connection);
		PowerMockito.when(connection.timeout(anyInt())).thenReturn(connection);
		PowerMockito.when(connection.maxBodySize(anyInt())).thenReturn(connection);
		PowerMockito.when(connection.get()).thenReturn(document);

		JsoupParser parser = new JsoupParser();
		Page res = parser.parse(testUrl);

		assertEquals("Sample page", res.getTitle());
		assertNotEquals(-1, res.getText().indexOf("sample text"));
		assertEquals(3, res.getLinks().size());
		assertTrue(res.getLinks().contains("http://test.com"));
		assertTrue(res.getLinks().contains("http://sample.com/sample/test2.html"));
		assertTrue(res.getLinks().contains("http://sample.com/test1.html"));
	}
	
	@Test
	public void parseInvalidUrl(){
		JsoupParser parser = new JsoupParser();
		Page res = parser.parse("aijoaijwd");
		assertNull(res);
	}

}
