package com.pawansharma.webcrawler;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.select.Elements;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class WebCrawler{
	
		static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36"; 
		static ArrayList<String> navURL = new ArrayList<String>();
		static String finalURL=null;
	    static Document doc = null;
	    static Logger testLogger = LogManager.getLogger(Logger.class.getName());
	
		public static void main(String[] args) throws IOException
		{
			String[] conceptName = new String[]{"homecentre","babyshopstores", "lifestyleshops", "splashfashions", "shoemartstores", "centrepointstores","homeboxstores","maxfashion"};
			String [] siteLocale = new String[] {"ae","sa","bh"};
			String [] langLocale = new String[] {"en","ar"};
			
			for(int i=0; i<conceptName.length; i++)
			{
				
				for(int j=0; j<siteLocale.length; j++)
				{
					for(int k=0; k<langLocale.length; k++) {
						
						if(conceptName[i] != "mothercarestores"){
							getHTMLDocument(conceptName[i].concat(".com/").concat(siteLocale[j]).concat("/").concat(langLocale[k]));
							getURLLinks(siteLocale[j]);
							getURLResponse();
						}
						else{
							getHTMLDocument("mothercarestores".concat(".com/").concat(siteLocale[j]).concat("/").concat(langLocale[k]));
							getURLLinks(siteLocale[j].concat("/").concat(langLocale[k]));
							getURLResponse();
							
						}
					  }
				}
			}
		}	
			
		private static void getHTMLDocument(String url)
		{
			try{
			 doc = Jsoup.connect("http://"+url)
							.userAgent(USER_AGENT)
							.ignoreHttpErrors(false)
							.followRedirects(true)
							.timeout(0)
							.get();
			 
			}catch(IOException ex)
			{
				testLogger.error(ex.getMessage());
			}
			
			
		}
	
		private static void getURLLinks(String pathname)
		{
			Elements navLinks = doc.select("a[href]");
			for(Element link : navLinks){
				if(link.absUrl("href") ==null || link.absUrl("href").isEmpty() || link.absUrl("href").startsWith("mail") || link.absUrl("href").endsWith("#")){
				 testLogger.info("The URL is"+link.absUrl("href"));				
				}
				else if(link.absUrl("href").contains(pathname)){
					navURL.add(link.absUrl("href"));
				}
	
			}
			
		}
	
	private static void getURLResponse()
	{ 
		try{
			for(String linkURL : navURL)
			{    finalURL=linkURL;
				 Response response = Jsoup.connect(linkURL)
						              .userAgent(USER_AGENT)
						              .timeout(0)
							          .ignoreHttpErrors(false)
							          .followRedirects(true)
							          .ignoreContentType(false)
							          .execute();
				  
				 
				 if(response.statusCode() == 200)
				 {     
					  testLogger.info(response.statusCode()+" - "+response.statusMessage()+"  :  "+linkURL+"\n");
	
			     }
				 
				 else
				 {
					 testLogger.error(response.statusCode()+" - "+response.statusMessage()+"  :  "+linkURL+"\n");
					 navURL.remove(finalURL);
					 break;
				 }
				
		       } 
			    navURL.clear();
				testLogger.info("All stored URL Links have been removed");
			}catch(Exception ex)
		
		      { testLogger.error("Exception - "+finalURL+"  "+ ex.getMessage());
				if(navURL.remove(finalURL)){
					testLogger.info("The link - "+finalURL+"  has removed");
				}
			  }
		
	    }

	
	
}