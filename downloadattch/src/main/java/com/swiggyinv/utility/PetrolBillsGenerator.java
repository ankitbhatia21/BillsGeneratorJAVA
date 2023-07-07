/**
* 
*/
package com.swiggyinv.utility;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

public class PetrolBillsGenerator {
	static char currentAlphabet = 'A';

	public static void main(String[] args) throws IOException {
		// Initialize variables
		int billsRequiredOf = 184000;
		int eachBillOf = 2000;
		int billsToCreate = billsRequiredOf / eachBillOf;
		int billsToCreateMonthly = billsToCreate / 12;
		int remainingBills = billsToCreate % 12;
		double[] petrolMonPrices = { 101.81, 105.41, 89.62, 96.72, 96.72, 96.72, 96.72, 96.72, 96.72, 96.72, 96.72,
				96.72 };
		LocalDate startDate = LocalDate.of(2022, Month.APRIL, 1);
		LocalDate endDate = LocalDate.of(2023, Month.MARCH, 1);
		int i = 0;

		// Create a new PDF document
		PdfDocument pdfDoc = new PdfDocument(new PdfWriter("D:/PetrolBills.pdf"));
		Document doc = new Document(pdfDoc, new PageSize(6f * 72, 6f * 72));
		// Add content to the first page
		Paragraph totalStat = new Paragraph("Total value of bills in this pdf are - Rs " + billsRequiredOf).setBold();
		totalStat.setTextAlignment(TextAlignment.LEFT);
		totalStat.setMargins(150, 50, 150, 50);
		doc.add(totalStat);

		// Add bills for 12 months in financial year
		for (LocalDate date = startDate; date.isBefore(endDate.plusMonths(1)); date = date.plusMonths(1)) {
			int year = date.getYear();
			int month = date.getMonthValue();
			double petrolMon = petrolMonPrices[i];
			i++;
			if (date.isEqual(startDate.plusMonths(2))) {
				int totalBillsNeed = billsToCreateMonthly + remainingBills;
				ArrayList<LocalDateTime> randomDateTimes2 = generateRandomDates(year, month, totalBillsNeed);
				List<String> randomNums2 = generateRandomNumbers(totalBillsNeed);
				for (int k = 0; k < totalBillsNeed; k++) {
					pdfDoc.addNewPage();
					addContent(doc, randomDateTimes2.get(k), randomNums2.get(k), petrolMon, eachBillOf);
				}
			} else {
				ArrayList<LocalDateTime> randomDateTimes = generateRandomDates(year, month, billsToCreateMonthly);
				List<String> randomNums = generateRandomNumbers(billsToCreateMonthly);
				for (int j = 0; j < billsToCreateMonthly; j++) {
					pdfDoc.addNewPage();
					addContent(doc, randomDateTimes.get(j), randomNums.get(j), petrolMon, eachBillOf);
				}
			}
		}

		// Close the document
		doc.close();
		System.out.println("PDF created successfully!");
	}
	
	/**
	 * Generates random dates for the given year and month.
	 * 
	 * @param year The year value.
	 * @param month The month value.
	 * @param size The number of dates to generate.
	 * @return An ArrayList of LocalDateTime objects representing the random dates.
	 */
	private static ArrayList<LocalDateTime> generateRandomDates(int year, int month, int size) {
		Set<LocalDate> set = new HashSet<>();
		while (set.size() < size) {
			int day = ThreadLocalRandom.current().nextInt(1, Month.of(month).length(Year.of(year).isLeap()));
			LocalDate date = LocalDate.of(year, month, day);
			set.add(date);
		}
		ArrayList<LocalDate> list = new ArrayList<>(set);
		ArrayList<LocalDateTime> finalres = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			LocalTime time = LocalTime.of(new Random().nextInt(17) + 7, new Random().nextInt(60));
			LocalDateTime dateTime = LocalDateTime.of(list.get(i), time);
			finalres.add(dateTime);
		}

		// sort the list using the Collections.sort() method
		Collections.sort(finalres);
		return finalres;
	}
	
	/**
	 * Generates random numbers.
	 * 
	 * @param size The number of random numbers to generate.
	 * @return A List of Strings representing the generated random numbers.
	 */
	private static List<String> generateRandomNumbers(int size) {
		Set<Integer> setNums = new HashSet<>();
		while (setNums.size() < size) {
			setNums.add(ThreadLocalRandom.current().nextInt(1000, 9999));
		}
		List<Integer> listNums = new ArrayList<>(setNums);
		Collections.sort(listNums);
		List<String> result = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			String s = String.format("%c%d", currentAlphabet, listNums.get(i));
			result.add(s);

		}
		currentAlphabet++;
		return result;
	}
	
	/**
	 * Adds content to the document for a single bill.
	 * 
	 * @param doc The document object.
	 * @param inputDatetime The input LocalDateTime.
	 * @param invNO The invoice number.
	 * @param petrPrice The patrol price.
	 * @param eachBillVal The value of each bill.
	 * @throws IOException If an I/O error occurs.
	 */
	private static void addContent(Document doc, LocalDateTime inputDatetime, String invNO, double petrPrice,
			int eachBillVal) throws IOException {
		// Load the font
		doc.setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN)).setFontSize(11f);

		// Add the image
		Image img = new Image(ImageDataFactory.create("D:/logo.png"));
		img.setWidth(50);
		img.setHorizontalAlignment(HorizontalAlignment.CENTER);
		doc.add(img);

		// Add the title
		Paragraph title = new Paragraph("Welcomes you").setBold();
		title.setTextAlignment(TextAlignment.CENTER);
		doc.add(title);

		// Add the address
		Paragraph address = new Paragraph("NARAIN SERVICE STATION\n16/1 Mathura Road Faridabad HRY-121002");
		address.setTextAlignment(TextAlignment.CENTER);
		address.setMargin(0);
		doc.add(address);

		// Add "27/02/2023 19:48" text
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy\t\t\t\t\t\t\t\t\t\t\tHH:mm");
		String formattedDateTime = inputDatetime.format(formatter);
		Paragraph datetime = new Paragraph(formattedDateTime).setTextAlignment(TextAlignment.CENTER);
		datetime.setMargin(0);
		doc.add(datetime);

		// Add the invoice details
		String inv = "GST No\t\t\t  :\tM43010GH195260\nINVOICE NO\t:\t" + invNO
				+ "\nNOZZLE NO\t :\t2\nPRODUCT\t\t:\tPetrol\nDENSITY\t\t  :\t751.3Kg/m3";
		Paragraph invoice = new Paragraph(inv);
		invoice.setMarginLeft(82);
		doc.add(invoice);

		// Add the billing details
		// Create a DecimalFormat object with two decimal places
		DecimalFormat df = new DecimalFormat("#.##");
		double result = eachBillVal / petrPrice;
		String blng = "RATE\t\t  :\t" + petrPrice + "INR/Ltr\nVOLUME\t:\t" + df.format(result) + "Ltr\nAMOUNT\t:\t"
				+ eachBillVal + "INR";
		Paragraph billing = new Paragraph(blng);
		billing.setMarginLeft(82);
		doc.add(billing);

		// Add the vehicle details
		Paragraph vehicle = new Paragraph("VEHICLE NO:\tNot Entrd");
		vehicle.setMarginLeft(82);
		doc.add(vehicle);

		// Add the closing message
		Paragraph closing = new Paragraph("Thank you! Please Visit Again");
		closing.setTextAlignment(TextAlignment.CENTER);
		doc.add(closing);

	}
}
