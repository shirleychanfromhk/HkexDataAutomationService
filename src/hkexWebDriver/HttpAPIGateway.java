package hkexWebDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class HttpAPIGateway{
	
	private static String user_agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.104 Safari/537.36";
	public static String method_get = "GET";
	
	static {
		// Configure System Environment
        System.setProperty("https.proxyPort", "8080");
    }

	public static FinancialDataBean getResult(String urlStr, String method) throws Exception {
		final Logger logger = Logger.getLogger(HttpAPIGateway.class.getName());
		String result = null;		
		logger.info("API Call URL: " + urlStr);
		
		try {
			/*
			 * Configure internet connection
			 */
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(20000);
			conn.setRequestProperty("Content-Type", "application/javascript");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("user-agent", user_agent);
			conn.setUseCaches(false);
			
			if(method.equalsIgnoreCase(method_get)) {
				conn.setRequestMethod(method_get);
			} 

			logger.info("Response Code : " + conn.getResponseCode());
			if (conn.getResponseCode() == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				result = response.toString();
			}else if(conn.getResponseCode() == 404) {
				System.out.println("The date is invalid. Please try again and restart the program.");
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			logger.info("Fail to connect url: " + urlStr);
			throw e;
		} catch (IOException e) {
			logger.info("Fail to connect url: " + urlStr);
			throw e;
		}	
		
		result = result.substring(10); //convert the respond to json result
		FinancialDataBean financialDataBean = new FinancialDataBean();
		List<String> marketList = new ArrayList<>();
		ArrayList<List> dailyDataArrayList = new ArrayList<>();
		ArrayList<List> stockDataArrayList = new ArrayList<>();
		List<String> subTitleDailyList = new ArrayList<>();
		List<String> subTitleStockList = new ArrayList<>();
		
		JSONArray json = new JSONArray(result);
		for(int index = 0; index < json.length(); index++) {
		    JSONObject jsonObject = json.getJSONObject(index);
		    String market = jsonObject.getString("market");
		    marketList.add(market);
		    logger.info("market: " + market );
		    
		    JSONArray content = jsonObject.getJSONArray("content");
		    for(int i = 0; i < content.length(); i++) {
		    	 JSONObject jsonObj = content.getJSONObject(i);
		    	 JSONObject table = jsonObj.getJSONObject("table");
		     	 int style = jsonObj.getInt("style");
		    	 JSONArray subTitle = table.getJSONArray("schema"); 
		    	 JSONArray obj = subTitle.getJSONArray(0); //JSON array only has one item 
		    	 JSONArray tr = table.getJSONArray("tr"); 
		    	 /*
		    	 * style = 1 refer to table of HISTORICAL DAILY, style = 2 refer to table of TOP 10 MOST ACTIVELY TRADED STOCKS
		    	 */ 
		    	 if(style == 1) {
			    	 for(int subTitleIndex = 0; subTitleIndex < obj.length(); subTitleIndex++) {
			    		 String ans = obj.getString(subTitleIndex);		    	
			    		 subTitleDailyList.add(ans);
			    	 }
			    	 
			    	 List<String> dailyDataList = new ArrayList<>(); 
			    	 for(int j = 0; j < obj.length(); j++) {			    		
			    		 JSONObject tdObj = tr.getJSONObject(j);
			    		 JSONArray td = tdObj.getJSONArray("td"); 
			    		 JSONArray tdArray = td.getJSONArray(0); //JSON array only has one item 
			    		 String data = tdArray.getString(0);
			    		 dailyDataList.add(data);			 
			    	 }
			    	 dailyDataArrayList.add(dailyDataList);
		    	 }else {
			    	 for(int subTitleIndex = 0; subTitleIndex < obj.length(); subTitleIndex++) {
			    		 String ans = obj.getString(subTitleIndex);
			    		 subTitleStockList.add(ans);
			    	 }
			    	 /**
			    	  * every ten stocks are grouped by an array, each array put in an array list and ordering
			    	  */
					 List<String> stockDataList = new ArrayList<>(); 
		    		 for(int stockCounter = 0; stockCounter < 10; stockCounter++) {
			    		 JSONObject tdObj = tr.getJSONObject(stockCounter);
			    		 JSONArray td = tdObj.getJSONArray("td"); 
			    		 JSONArray tdArray = td.getJSONArray(0);
			    		 for(int tdArrayIndex = 0; tdArrayIndex < tdArray.length(); tdArrayIndex++) {
				    		 String data = tdArray.getString(tdArrayIndex);
				    		 stockDataList.add(data);
			    		 }
		    		 }
		    		 stockDataArrayList.add(stockDataList);
		    	 }
		    	 logger.info("dailyDataArrayList: "+ dailyDataArrayList);
	    		 logger.info("stockDataList: "+ stockDataArrayList);
		   }
		}
		financialDataBean.setMarkets(marketList);
		financialDataBean.setDailySubTitles(subTitleDailyList);
		financialDataBean.setStockSubTitles(subTitleStockList);
		financialDataBean.setDailyDataList(dailyDataArrayList);
		financialDataBean.setStockDataList(stockDataArrayList);		
		return financialDataBean;
	}	
}
