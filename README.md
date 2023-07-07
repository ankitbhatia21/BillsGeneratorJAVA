# BillsGeneratorJAVA

It has 2 utilities :

**1. PetrolBillsGenerator:** The purpose of this code is to generate fake petrol bills in PDF format for a specified number of months in a financial year. The bills are randomly generated, with the date, invoice number, petrol price, volume, and amount all being randomly generated. The PDF file also includes a logo, title, address, and closing message. 

**2. GmailDownloadAttch/Swiggy bills downloader:** The purpose of this code is to download emails (SWIGGY bills) from Gmail that match specific criteria, such as the sender, subject keyword, and date range. And manipulates dates in those bills. It utilizes the Gmail API and Google OAuth 2.0 for authentication and authorization. The code fetches emails that meet the specified criteria, extracts the HTML content from the messages, processes the content, and generates a PDF containing the extracted information. The PDF also includes the total value of bills found in the emails.

This code performs the following steps:

i. Set up the necessary variables and constants:

APPLICATION_NAME: The name of the application.
JSON_FACTORY: The JSON factory for creating JSON objects.
TOKENS_DIRECTORY_PATH: The path to the directory where authorization tokens are stored.
CREDENTIALS_FILE_PATH: The path to the client secret file.
SCOPES: The list of Gmail scopes required for reading emails.
USER_ID: The ID of the user (in this case, "me" indicates the authenticated user's email).
from_email_address: The email address of the sender to filter messages.
startDate and endDate: The start and end dates for filtering messages.

ii. The main method:

Builds an authorized Gmail API client service using the APPLICATION_NAME, JSON_FACTORY, and getCredentials method.
Uses the Gmail API client to retrieve a list of messages matching the specified criteria.
Retrieves the HTML contents of the messages and stores them in a list.
Reverses the order of the HTML contents for sorting purposes.
Parses the HTML contents, extracts the order total from each message, and calculates the total sum of the bills.
Combines the HTML contents with additional HTML for displaying the total value of bills.
Converts the combined HTML to a PDF file using iText PDF library.
Prints a completion message.

iii. The getCredentials method:

Loads the client secrets from the CREDENTIALS_FILE_PATH.
Builds a Google authorization code flow with the specified parameters.
Authorizes the user and returns the authorized credential.

iv. Set up your Gmail API credentials:

Create a project on the Google Cloud Platform (https://console.cloud.google.com/).
Enable the Gmail API for your project.
Create OAuth 2 credentials (select desktop app) and download client_secret.json.

Note about tokens : OAuth tokens expire in every few days. After token expiry, delete token file under **D:/tokens** folder and it will be regenerated/refreshed when you will run the code next time & verify your access.





