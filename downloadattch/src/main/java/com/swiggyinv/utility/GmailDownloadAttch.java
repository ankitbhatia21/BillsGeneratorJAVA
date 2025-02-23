package com.swiggyinv.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;

public class GmailDownloadAttch {
	private static final String APPLICATION_NAME = "GmailDownloadAttch";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "D:/tokens";
	private static final String CREDENTIALS_FILE_PATH = "D:/client_secret.json";
	private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_READONLY);
	private static final String USER_ID = "me";
	private static final String from_email_address = "noreply@swiggy.in";
//	private static final String startDate = "2022-01-01T00:00:00Z";
//	private static final String endDate = "2022-12-31T23:59:59Z";
	private static final String SUBJECT_KEYWORD = "Your Swiggy order";
	private static final String startDate = "2023-04-01";
	private static final String endDate = "2024-04-01";

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();

		ListMessagesResponse response = service.users().messages().list(USER_ID).setQ("from:" + from_email_address
				+ " subject:" + SUBJECT_KEYWORD + " after:" + startDate + " before:" + endDate)
				.setMaxResults(new Long(50)).execute();
		List<Message> messages = new ArrayList<>();
		while (response.getMessages() != null) {
			messages.addAll(response.getMessages());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service
						.users().messages().list(USER_ID).setQ("from:" + from_email_address + " subject:"
								+ SUBJECT_KEYWORD + " after:" + startDate + " before:" + endDate)
						.setPageToken(pageToken).setMaxResults(new Long(50)).execute();
			} else {
				break;
			}
		}

		System.out.println(" Gmail messages count - " + messages.size());

		List<String> htmlContents = new ArrayList<>();
		for (Message message : messages) {
		    Message msg = service.users().messages().get(USER_ID, message.getId()).setFormat("full").execute();
		    MessagePart payload = msg.getPayload();
		    processPayload(payload, htmlContents);
		}

		Collections.reverse(htmlContents); // for sorting 
		
		String htmlStrForPDF = "";
		double totalSum = 0.0;
		for (String msg : htmlContents) {
			String output = msg.replaceAll("\\b2024\\b", "2025");
			String html = output.replaceAll("\\b2023\\b", "2024");
			htmlStrForPDF += html;

			Document doc = Jsoup.parse(html);
			Element orderTotalElem = doc.select("tr.grand-total td").last();
			if (orderTotalElem != null) {
				String orderTotalStr = orderTotalElem.text().trim().replace("₹", "");
				double orderTotal = Double.parseDouble(orderTotalStr);
				totalSum += orderTotal;
			}

		}
		String totals = "<!DOCTYPE html>\r\n" + "<html>\r\n" + "  <head>\r\n"
				+ "    <title>Total value of bills</title>\r\n" + "  </head>\r\n" + "  <body>\r\n"
				+ "    <p><strong>Total value of bills in this pdf are -</strong> Rs  " + totalSum + "</p>\r\n"
				+ "  </body>\r\n" + "</html>\r\n" + "";
		String htmlStrForPDF2 = totals + htmlStrForPDF;
		PdfWriter writer = new PdfWriter("D:/BillsGmail.pdf");
		HtmlConverter.convertToPdf(htmlStrForPDF2, writer);
		System.out.println(" Execution complete ");

	}
	
	/**
     * Retrieves the Gmail API credentials using the provided HTTP transport.
     *
     * @param HTTP_TRANSPORT The HTTP transport to use.
     * @return The authorized Gmail API credentials.
     * @throws IOException If an I/O error occurs.
     */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

		// Load client secrets.
		InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
		// returns an authorized Credential object.
		return credential;
	}
	
	private static void processPayload(MessagePart payload, List<String> htmlContents) {
	    String mimeType = payload.getMimeType();
	    System.out.println(mimeType);

	    if (mimeType.equals("text/html")) {
	        // Handle HTML content
	        byte[] data = payload.getBody().decodeData();
	        String html = new String(data, StandardCharsets.UTF_8);
	        htmlContents.add(html);
	    } else if (mimeType.startsWith("multipart/")) {
	        // Handle multipart content (e.g., multipart/mixed, multipart/alternative)
	        List<MessagePart> parts = payload.getParts();
	        if (parts != null) {
	            for (MessagePart part : parts) {
	                processPayload(part, htmlContents); // Recursively process each part
	            }
	        }
	    }
	}
}
