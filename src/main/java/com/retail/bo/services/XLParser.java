package com.retail.bo.services;

import com.retail.bo.model.Item;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class XLParser {

    public List<Item> getItemsFromXlxToMap(File file) throws Exception {

        FileInputStream fileInputStream = new FileInputStream(file);

        XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = workBook.getSheetAt(0);

        List<Item> items = new ArrayList<>();
        double ean1 = 0.0, lotId1 = 0.0, batchNumber1 = 0.0;
        String ean = "", lotId = "", batchNumber = "";
        Iterator<Row> rowIterator = sheet.rowIterator();
        for (int i = 0; rowIterator.hasNext(); i++) {
            Row row = rowIterator.next();
            if (i > 0 && row.getCell(0) != null) {
            	int eanDataType = row.getCell(0).getCellType();
            	if(eanDataType == 1) {
            		ean = row.getCell(0).getStringCellValue();
            	}else {
            		ean1 = row.getCell(0).getNumericCellValue();
            		int eanInt = (int) ean1;
                	ean = String.valueOf(eanInt);
            	}
                String productName = row.getCell(1).getStringCellValue();
                String description = row.getCell(2).getStringCellValue();
                int lotIdType = row.getCell(3).getCellType();
                if(lotIdType == 1) {
            		lotId = row.getCell(3).getStringCellValue();
            	}else {
            		lotId1 = row.getCell(3).getNumericCellValue();
            		int lotIdInt = (int) lotId1;
            		lotId = String.valueOf(lotIdInt);
            	}
                if(row.getCell(4) != null) {
                	int batchNumberType = row.getCell(4).getCellType();
                    if(batchNumberType == 1) {
                    	batchNumber = row.getCell(4).getStringCellValue();
                	}else {
                		batchNumber1 = row.getCell(4).getNumericCellValue();
                		int batchNumberInt = (int) batchNumber1;
                		batchNumber = String.valueOf(batchNumberInt);
                	}
                }
                
                double quantity = row.getCell(5).getNumericCellValue();
                double mrp = row.getCell(6).getNumericCellValue();
                double sp = row.getCell(7).getNumericCellValue();
                double cp = row.getCell(8).getNumericCellValue();
                String expiryDate = "", manufacturingDate = "";
                try{
                	if(row.getCell(9) != null) {
                		manufacturingDate = new SimpleDateFormat("yyyy-MM-dd").format(row.getCell(9).getDateCellValue());
                	}
                	if(row.getCell(10) != null) {
                		expiryDate = new SimpleDateFormat("yyyy-MM-dd").format(row.getCell(10).getDateCellValue());
                	}
                }catch(Exception e){
                    
                }                
                Item item = new Item(ean, productName, description, lotId, batchNumber, quantity, mrp, sp, cp, manufacturingDate, expiryDate);
                

                items.add(item);
            }
        }
        return items;
    }
}
