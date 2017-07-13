package com.farmers.farm;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.BeforeClass;
import org.junit.Test;

import com.farmers.farm.Challenge;
import com.farmers.farm.DataMap;

public class tests {
	
	public static String FILE_PATH;
	public static String FILE_NAME;
	public static String DUMMY_FILE_NAME;
	static DataMap dataMap;

	@BeforeClass
	public static void setupPaths() {
		FILE_PATH  = "data/";
		FILE_NAME  = "test_data.xlsx";
		DUMMY_FILE_NAME  = "dummy_test_data.xlsx";
		
	}
	
	@Test
	public void sourceFileExistsTrue(){
		String path = FILE_PATH + FILE_NAME;
		File shouldExist = new File(path);
	    assertTrue(shouldExist.exists());
	}
	
	public void createDummytestExcelFile(){
		XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        Object[][] datatypes = {
                {"Location", "Item", "Day", "Par"},
                {"525_W_MONROE", "Phoenix Bean Tofu", "Mon", 2},
                {"525_W_MONROE", "Phoenix Bean Tofu", "Tue", 2},
                {"525_W_MONROE", "Phoenix Bean Tofu", "Wed", 2},
                {"525_W_MONROE", "Phoenix Bean Tofu", "Thu", 2},
                {"525_W_MONROE", "Phoenix Bean Tofu", "Fri", 2},
                {"525_W_MONROE", "Phoenix Bean Tofu", "Sat", 2},
                {"525_W_MONROE", "Phoenix Bean Tofu", "Sun", 2}
                
        };
        
        int rowNum = 0;
        
        for (Object[] datatype : datatypes) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object field : datatype) {
                Cell cell = row.createCell(colNum++);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                }
            }
        }
        
        try {
            FileOutputStream outputStream = new FileOutputStream(DUMMY_FILE_NAME);
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}
	
	
	@Test
	public void testReadXLFile(){
		createDummytestExcelFile();
		dataMap = Challenge.readXLFile(DUMMY_FILE_NAME);
		int datamapSize = dataMap.getData().size();
		assertEquals(7,datamapSize);
	}
	
	
	@Test
	public void testWriteXMLFile(){
		createDummytestExcelFile();
		dataMap = Challenge.readXLFile(DUMMY_FILE_NAME);
		Challenge.writeXMLFile(dataMap);
		
		try {
			long count = Files.find(
				    Paths.get("data/"), 
				    1,
				    (path, attributes) -> attributes.isDirectory()
				).count() - 1;
			
			assertEquals(7,count);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
}
