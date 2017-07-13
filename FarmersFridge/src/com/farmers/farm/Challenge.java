package com.farmers.farm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Challenge {

	public final static String FILE_PATH  = "data/";
	public final static String FILE_NAME  = "test_data.xlsx";
	public static Map<String,String> days; 
	
	public static void main(String[] args) {
		mapdays();
		DataMap dataMap = readXLFile(FILE_PATH + FILE_NAME);
		writeXMLFile(dataMap);
	}

	private static void mapdays() {
		days = new HashMap<>();
		days.put("monday", "Mon");
		days.put("tuesday", "Tue");
		days.put("wednesday", "Wed");
		days.put("thursday", "Thu");
		days.put("friday", "Fri");
		days.put("saturday", "Sat");
		days.put("sunday", "Sun");
	}

	/*
    Return DataMap, populated with all entries from  test_data.xlsx if present.
    Eg. input:
         @file= "test_data.xlsx";
        output:
         @Datamap
	 */
	public static DataMap readXLFile( String file) {
		DataMap dataMap = new DataMap();
		try {

			FileInputStream excelFile = new FileInputStream(new File(file));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();

			// First row has title, skipping it
			iterator.next();

			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				populateDataMap(currentRow, dataMap);
				workbook.close();
				excelFile.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("INPUT FILE not found !!!");
		} catch (IOException e) {
			System.out.println("IO Exception Occured");
		}
		return dataMap;
	}

	/*
    Inserting currentrow data into datamap, Only is neither cell is Empty. 
    Else it will considered as Invalid entry or currupted entry
    Eg. input:
         @Row = currentRow 
         @DataMap = dataMap
	 */
	
	private static void populateDataMap(Row currentRow, DataMap dataMap) {
		
		Cell c0 = currentRow.getCell(0);
		Cell c1 = currentRow.getCell(1);
		Cell c2 = currentRow.getCell(2);
		Cell c3 = currentRow.getCell(3);
		
		if (c0 == null || c1 == null || c2 == null || c3 == null) {
			return;
		}
		
		String location = currentRow.getCell(0).getStringCellValue();
		String item = currentRow.getCell(1).getStringCellValue();
		String d = currentRow.getCell(2).getStringCellValue().toLowerCase();
		if(!days.containsKey(d)){
			return;
		}
		
		String day = days.get(d);
		Double par = currentRow.getCell(3).getNumericCellValue();

		if (dataMap.getData().get(day) == null) {
			dataMap.getData().put(day, new HashMap<String, HashMap<String, Double>>());
		}

		if (dataMap.getData().get(day).get(location) == null) {
			dataMap.getData().get(day).put(location, new HashMap<String, Double>());
		}

		if (dataMap.getData().get(day).get(location).get(item) == null) {
			dataMap.getData().get(day).get(location).put(item, par);
		}
	}

	
	/*
    Reading datamap, and generatnig Xml files according to Day and then according to Location  
    Eg. input:
         @DataMap = dataMap
         output:
         folders with Day and XML inside folder for each location
	 */
	public static void writeXMLFile(DataMap dataMap) {

		for (Entry<String, HashMap<String, HashMap<String, Double>>> day : dataMap.getData().entrySet()) {
			File dayDir = new File(FILE_PATH + day.getKey());
			if (dayDir.mkdir()) {
				for (Entry<String, HashMap<String, Double>> location : day.getValue().entrySet()) {
					// create location file
					try {
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						
						Transformer transformer = transformerFactory.newTransformer();
						transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
						transformer.setOutputProperty(OutputKeys.INDENT, "yes");

						
						DOMSource source = new DOMSource(createXMLDocument(location));
						StreamResult result = new StreamResult(new File(FILE_PATH + day.getKey() + "/" + location.getKey()));
						transformer.transform(source, result);
						
					} catch (Exception e) {
						System.out.println("error while writing location file");
					}
				}
			}
		}
	}
	
	/*
    Generating one XML by reading one entry from HashMap, i.e. for specific Location.   
    Eg. input:
         @Entry = location
         output:
         Document of type XML
	 */

	private static Document createXMLDocument(Entry<String, HashMap<String, Double>> location) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("parMap");
			doc.appendChild(rootElement);

			// location name element
			Element locationElement = doc.createElement("name");
			locationElement.appendChild(doc.createTextNode(location.getKey()));
			rootElement.appendChild(locationElement);

			// location name element
			Element pars = doc.createElement("pars");
			rootElement.appendChild(pars);

			for (Entry<String, Double> items : location.getValue().entrySet()) {

				// firstname elements
				Element entry = doc.createElement("entry");

				Element key = doc.createElement("key");
				key.appendChild(doc.createTextNode(items.getKey()));
				entry.appendChild(key);

				Element value = doc.createElement("value");
				value.appendChild(doc.createTextNode(String.valueOf(items.getValue())));
				entry.appendChild(value);

				pars.appendChild(entry);
			}
			return doc;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
