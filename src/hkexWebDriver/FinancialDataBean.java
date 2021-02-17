package hkexWebDriver;

import java.util.ArrayList;
import java.util.List;

public class FinancialDataBean {
	public List<String> markets;
	public List<String> dailySubTitles;
	public List<String> stockSubTitles;
	public String market;
	public ArrayList<List> dailyDataList;
	public ArrayList<List> stockDataList;
	
	public List<String> getMarkets() {
		return markets;
	}
	public void setMarkets(List<String> markets) {
		this.markets = markets;
	}
	public String getMarket() {
		return market;
	}
	public void setMarket(String market) {
		this.market = market;
	}
	public List<String> getDailySubTitles() {
		return dailySubTitles;
	}
	public void setDailySubTitles(List<String> dailySubTitles) {
		this.dailySubTitles = dailySubTitles;
	}
	public List<String> getStockSubTitles() {
		return stockSubTitles;
	}
	public void setStockSubTitles(List<String> stockSubTitles) {
		this.stockSubTitles = stockSubTitles;
	}
	public ArrayList<List> getStockDataList() {
		return stockDataList;
	}
	public void setStockDataList(ArrayList<List> stockDataList) {
		this.stockDataList = stockDataList;
	}
	public ArrayList<List> getDailyDataList() {
		return dailyDataList;
	}
	public void setDailyDataList(ArrayList<List> dailyDataList) {
		this.dailyDataList = dailyDataList;
	}

}
