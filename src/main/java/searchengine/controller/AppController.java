package searchengine.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import searchengine.services.AppService;
 
@Controller
public class AppController {
	
	private static final Logger logger = Logger.getLogger(AppController.class);
	private final AppService service;

	@Autowired
	public AppController(AppService service) {
		this.service = service;
	}
	
    @RequestMapping(value="/index", method=RequestMethod.GET)
    public String loadIndexPage(Model m) {
    	return "index";
    }

    @RequestMapping(value="/index", method=RequestMethod.POST)
    public String submitIndex(@RequestParam("url") String url, @RequestParam("depth") String depth, Model m) {
    	if (isValidUrl(url) && isValidDepth(depth)) {
    		List<String> indexedUrls = service.indexUrl(url, Integer.valueOf(depth));
        	m.addAttribute("indexedUrls", indexedUrls);
    	} else {
    		m.addAttribute("error", "Invalid parameter url or depth");
    	}
        return "index";
    }

    @RequestMapping(value={"/","/search"}, method=RequestMethod.GET)
    public String loadSearchPage(Model m) {
        return "search";
    }

    @RequestMapping(value="/search", method=RequestMethod.POST)
    public String submitSearch(@RequestParam("q") String query, Model m) {
        if (isValidQuery(query)){
        	m.addAttribute("pages", service.getPages(query));
        } else {
        	m.addAttribute("error", "Invalid query");
        	return "search";
        }
    	return "searchresult";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleExceptions(Exception e, Model m) {
        logger.error("Exception :", e);
    	m.addAttribute("message", e.getMessage());
        return "error";
    }
    
    private boolean isValidUrl(String url){
    	try{
    		new URL(url);
    	}catch (MalformedURLException e){
    		return false;
    	}
    	return true;
    }
 
    private boolean isValidDepth(String depth){
    	try{
    		if (Integer.valueOf(depth) < 0) return false;
    	}catch (NumberFormatException e){
    		return false;
    	}
    	return true;
    }

    
    private boolean isValidQuery(String query){
    	return (query == null || query.isEmpty()) ? false : true;
    }
    

}