package hkexWebDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class FinancialDataAutomationService extends HttpAPIGateway {
	public static void programRun() {
		/*
		 * Menu & Input validation
		 */
		System.out.print("Please enter the date in (YYYYMMDD) format: ");
		String input = "";

		try {
			Scanner inputScanner = new Scanner(System.in);
			input = inputScanner.next().trim();
			inputScanner.close();
			Date formatDate = new SimpleDateFormat("yyyyMMdd").parse(input);
			SimpleDateFormat now = new SimpleDateFormat("yyyyMMdd");
			String date = now.format(new Date());	
			
			if (input.equals(new SimpleDateFormat("yyyyMMdd").format(formatDate)) && input.length() == 8) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(formatDate);
				if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
					System.out.println("Saturday does not has data");
					throw new Exception();
				} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					System.out.println("Sunday does not has data");
					throw new Exception();
				}else {	
					System.out.println("Input: " + input);
				}
			}
		} catch (Exception e) {
			System.out.println("Please try other date and restart the program");
			System.exit(0);
		}
		System.out.println("Starting the program");

		/*
		 * Declaring folder storage path and access url
		 */
		String pathToFolder = "E:/project/hkexWebDriver_result/" + input + "/";
		String url = "data_tab_daily_" +input + "e.js";
		
		/*
		 * Extract data from web page & Create Excel worksheet
		 */
		try {
			FinancialDataBean result = HttpAPIGateway.getResult("https://www.hkex.com.hk/eng/csm/DailyStat/"+url, HttpAPIGateway.method_get);
			ReadDailyData(result, input);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void ReadDailyData(FinancialDataBean financialDataBean, String input) {
		List<String> marketList = financialDataBean.getMarkets();
		List<String> dailySubTitleList = financialDataBean.getDailySubTitles();
		List<String> stockSubTitleList = financialDataBean.getStockSubTitles();
		ArrayList<List> dataList = financialDataBean.getDailyDataList();
		ArrayList<List> stockDataList = financialDataBean.getStockDataList();
		//List<String> stockItem = new ArrayList<String>();
				
		String pathToFolder = "E:/project/hkexWebDriver_result/" + input + "/";
		File xlsxFile = new File(pathToFolder + "Hkex Historical Daily " + input + ".xlsx");
		int row = 0;
		/*
		 * Writing all the data to .xlsx file
		 */
		XSSFWorkbook workBook = new XSSFWorkbook();

		XSSFSheet sheet1 = workBook.createSheet("Hkex Historical Daily"); //define sheet name
		XSSFRow currentRow = sheet1.createRow(row);
		XSSFFont titleFont = workBook.createFont();
		titleFont.setBold(true);
		titleFont.setFontName("·s²Ó©úÅé");
		titleFont.setFontHeightInPoints((short) 14);
		XSSFCellStyle titleCellStyle = workBook.createCellStyle();
		titleCellStyle.setFont(titleFont);

		XSSFCell titleCell = currentRow.createCell(0);
		titleCell.setCellValue("Hkex Historical Daily"); //define sheet title
		titleCell.setCellStyle(titleCellStyle);
		row+=1; 

		for(int marketCounter=0; marketCounter<4;marketCounter++) {
			currentRow = sheet1.createRow(row);
			sheet1.setDefaultColumnWidth(15);
			currentRow.createCell(0).setCellValue(marketList.get(marketCounter));
			row+=1; 
			currentRow = sheet1.createRow(row);
			
			List<String> dailyItem = dataList.get(marketCounter); 
			/*
			 * market = SSE NorthBond or SZSE NorthBond's daily subtitle different with other's daily subtitle
			 */
			if("SSE Northbound".equals(marketList.get(marketCounter)) || "SZSE Northbound".equals(marketList.get(marketCounter))){
				for(int dailySubTitleIndex =0; dailySubTitleIndex < 8; dailySubTitleIndex++) {
					currentRow.createCell(dailySubTitleIndex).setCellValue(dailySubTitleList.get(dailySubTitleIndex));
				}
				row+=1; 
				currentRow = sheet1.createRow(row);	
				for(int dailyItemIndex = 0; dailyItemIndex < dailyItem.size(); dailyItemIndex++) {
					currentRow.createCell(dailyItemIndex).setCellValue(dailyItem.get(dailyItemIndex));
				}
				row+=1;
				currentRow = sheet1.createRow(row);		
			}else {
				currentRow = sheet1.createRow(row);
				row+=1;
				for(int dailySubTitleIndex = 0; dailySubTitleIndex < 6;dailySubTitleIndex++) {
					currentRow.createCell(dailySubTitleIndex).setCellValue(dailySubTitleList.get(dailySubTitleIndex));
				}
				currentRow = sheet1.createRow(row);
				row+=1;
				for(int counter = 0; counter < dailyItem.size(); counter++) {
					currentRow.createCell(counter).setCellValue(dailyItem.get(counter));
				}
			}
			currentRow = sheet1.createRow(row);
			row+=1;
			for(int stockSubTitleListIndex =0;  stockSubTitleListIndex < 6;stockSubTitleListIndex++) {
				currentRow.createCell(stockSubTitleListIndex).setCellValue(stockSubTitleList.get(stockSubTitleListIndex));
			}
			currentRow = sheet1.createRow(row);
			row+=1;
			List<String> stockItem = stockDataList.get(marketCounter); 
			for(int columnCounter = 0; columnCounter < stockItem.size(); columnCounter+=6) {	
				sheet1.setColumnWidth(2, 13000); // some company name too long so the column width need to adjust
				currentRow.createCell(0).setCellValue(stockItem.get(columnCounter));
				currentRow.createCell(1).setCellValue(stockItem.get(columnCounter+1));
				currentRow.createCell(2).setCellValue(stockItem.get(columnCounter+2));
				currentRow.createCell(3).setCellValue(stockItem.get(columnCounter+3));
				currentRow.createCell(4).setCellValue(stockItem.get(columnCounter+4));
				currentRow.createCell(5).setCellValue(stockItem.get(columnCounter+5));
				currentRow = sheet1.createRow(row);
				row+=1;
			}	
		}
		System.out.println("Processing....");
		/*
		 * create a excel file to destination folder
		 */
		try {
			xlsxFile.getParentFile().mkdirs();
			xlsxFile.createNewFile();
			workBook.write(new FileOutputStream(xlsxFile));
			workBook.close();
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("Fail to create Excel report");
			System.exit(0);
		}
		System.out.println("Success to create Excel report");
	}
}
