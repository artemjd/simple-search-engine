package searchengine.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import searchengine.controller.AppController;
import searchengine.model.Page;
import searchengine.services.AppService;

public class SearchEngineControllerTest {

	private MockMvc mockMvc;

	@Mock
	private AppService service;

	@InjectMocks
	private AppController controller;

	@Before
	public void setup() throws Exception {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/view/");
		viewResolver.setSuffix(".jsp");
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders
				.standaloneSetup(new AppController(service))
				.setViewResolvers(viewResolver).build();
	}

	@Test
	public void testIndexValidUrl() throws Exception {
		when(service.indexUrl(anyString(), anyInt())).thenReturn(
				Arrays.asList("http://sample.com"));
		mockMvc.perform(
				post("/index").param("depth", "1").param("url",
						"http://test.com")).andExpect(status().isOk())
				.andExpect(forwardedUrl("/WEB-INF/view/index.jsp"))
				.andExpect(model().attributeExists("indexedUrls"));
	}

	@Test
	public void testIndexEmptyUrl() throws Exception {
		when(service.indexUrl(anyString(), anyInt())).thenReturn(null);
		mockMvc.perform(post("/index").param("depth", "1").param("url", ""))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl("/WEB-INF/view/index.jsp"))
				.andExpect(model().attributeExists("error"));
	}

	@Test
	public void testIndexInvalidDepth() throws Exception {
		when(service.indexUrl(anyString(), anyInt())).thenReturn(null);
		mockMvc.perform(post("/index").param("depth", "-1").param("url", "http://sample.com"))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl("/WEB-INF/view/index.jsp"))
				.andExpect(model().attributeExists("error"));
		when(service.indexUrl(anyString(), anyInt())).thenReturn(null);
		mockMvc.perform(post("/index").param("depth", "a").param("url", "http://sample.com"))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl("/WEB-INF/view/index.jsp"))
				.andExpect(model().attributeExists("error"));
	}

	@Test
	public void testSearchInvalidQuery() throws Exception {
		when(service.getPages(anyString())).thenReturn(null);
		mockMvc.perform(post("/search").param("q", ""))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl("/WEB-INF/view/search.jsp"))
				.andExpect(model().attributeExists("error"));
	}

	@Test
	public void testSearchValidQuery() throws Exception {
		List<Page> results = new ArrayList<Page>();
		results.add(new Page("http://sample.com", "Sample", null, null));
		when(service.getPages(anyString())).thenReturn(results);
		mockMvc.perform(post("/search").param("q", "query"))
				.andExpect(status().isOk())
				.andExpect(forwardedUrl("/WEB-INF/view/searchresult.jsp"))
				.andExpect(model().attributeExists("pages"));
	}

	@Test
	public void testException() throws Exception {
		when(service.indexUrl(anyString(), anyInt())).thenThrow(
				new RuntimeException());
		mockMvc.perform(
				post("/index").param("depth", "1").param("url",
						"http://test.com")).andExpect(status().isOk())
				.andExpect(forwardedUrl("/WEB-INF/view/error.jsp"));
	}

}