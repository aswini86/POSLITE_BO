package com.retail.bo.services;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class ExportXLS{

	public static ByteArrayInputStream buildExcelDocument(XSSFWorkbook workbook,HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// change the file name
	    response.setHeader("Content-Disposition", "attachment; filename=\"products.xls\"");
	    
	    // create excel xls sheet
	    Sheet sheet = workbook.createSheet("Product_Detail");
	    //Sheet sheet = workbook.createSheet();
	    sheet.setDefaultColumnWidth(30);

	    // create style for header cells
	    CellStyle style = workbook.createCellStyle();
	    Font font = workbook.createFont();
	    font.setFontName("Arial");
	    style.setFillForegroundColor(HSSFColor.BLUE.index);
	    //style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    //font.setBold(true);
	    font.setColor(HSSFColor.WHITE.index);
	    style.setFont(font);


	    // create header row
	    Row header = sheet.createRow(0);
	    header.createCell(0).setCellValue("Ean");
	    header.getCell(0).setCellStyle(style);
	    header.createCell(1).setCellValue("Product Name");
	    header.getCell(1).setCellStyle(style);
	    header.createCell(2).setCellValue("Description");
	    header.getCell(2).setCellStyle(style);
	    header.createCell(3).setCellValue("Lot Id");
	    header.getCell(3).setCellStyle(style);
	    header.createCell(4).setCellValue("Quantity");
	    header.getCell(4).setCellStyle(style);
	    header.createCell(5).setCellValue("Mrp");
	    header.getCell(5).setCellStyle(style);
	    header.createCell(6).setCellValue("SP");
	    header.getCell(6).setCellStyle(style);
	    header.createCell(7).setCellValue("CP");
	    header.getCell(7).setCellStyle(style);
	    header.createCell(8).setCellValue("Expiry Date");
	    header.getCell(8).setCellStyle(style);
	    
	    workbook.write(out);
	    return new ByteArrayInputStream(out.toByteArray());

	}
	
}