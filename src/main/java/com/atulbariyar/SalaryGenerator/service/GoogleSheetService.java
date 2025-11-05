//package com.atulbariyar.SalaryGenerator.service;
//
//
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.model.ValueRange;
//import com.google.auth.http.HttpCredentialsAdapter;
//import com.google.auth.oauth2.GoogleCredentials;
//import jakarta.annotation.PostConstruct;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.GeneralSecurityException;
//import java.util.List;
//
//@Service
//public class GoogleSheetService {
//
//    private Sheets sheetsService;
//
//    // Replace with your actual Google Sheet ID
//    private static final String SPREADSHEET_ID = "1YIbMSq0w2n6Y_KwvbpTYFrT0tdDpNxsPncCHXvRfg8Y";
//
//    @PostConstruct
//    public void init() {
//        try {
//            sheetsService = getSheetsService();
//            System.out.println("✅ Google Sheets service initialized successfully!");
//        } catch (Exception e) {
//            System.err.println("❌ Error initializing Google Sheets service: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
//        InputStream credentialsStream = new ClassPathResource("credentials.json").getInputStream();
//
//        GoogleCredentials credentials = GoogleCredentials
//                .fromStream(credentialsStream)
//                .createScoped(List.of("https://www.googleapis.com/auth/spreadsheets.readonly"));
//
//        return new Sheets.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(),
//                GsonFactory.getDefaultInstance(),
//                new HttpCredentialsAdapter(credentials)
//        )
//                .setApplicationName("Salary Slip Generator")
//                .build();
//    }
//
//    // ✅ This is the method your controller is trying to call
//    public List<List<Object>> readData(String range) {
//        try {
//            ValueRange response = sheetsService.spreadsheets().values()
//                    .get(SPREADSHEET_ID, range)
//                    .execute();
//
//            List<List<Object>> values = response.getValues();
//            if (values == null || values.isEmpty()) {
//                System.out.println("⚠️ No data found for range: " + range);
//                return List.of();
//            } else {
//                System.out.println("📄 Data retrieved for range: " + range);
//                return values;
//            }
//        } catch (Exception e) {
//            System.err.println("❌ Error reading Google Sheet: " + e.getMessage());
//            e.printStackTrace();
//            return List.of();
//        }
//    }
//
//    // Optional helper method for quick manual checks
//    public void printEmployeeData(String sheetName) {
//        String range = sheetName + "!A1:F";
//        List<List<Object>> values = readData(range);
//        values.forEach(System.out::println);
//    }
//}


package com.atulbariyar.SalaryGenerator.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;

@Service
public class GoogleSheetService {

    private Sheets sheetsService;

    @PostConstruct
    public void init() {
        try {
            String base64Credentials = System.getenv("GOOGLE_CREDENTIALS");

            if (base64Credentials == null || base64Credentials.isEmpty()) {
                throw new RuntimeException("GOOGLE_CREDENTIALS environment variable is missing");
            }

            byte[] decoded = Base64.getDecoder().decode(base64Credentials);

            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new ByteArrayInputStream(decoded))
                    .createScoped(List.of(SheetsScopes.SPREADSHEETS));

            sheetsService = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            )
                    .setApplicationName("SalaryGenerator")
                    .build();

            System.out.println("✅ Google Sheets Service Initialized Successfully");

        } catch (Exception e) {
            System.out.println("❌ Error initializing Google Sheets service: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<List<Object>> readData(String range) throws Exception {
        String sheetId = System.getenv("GOOGLE_SHEET_ID"); // Optional improvement
        if (sheetId == null || sheetId.isEmpty()) {
            throw new RuntimeException("GOOGLE_SHEET_ID environment variable is missing");
        }

        return sheetsService.spreadsheets().values()
                .get(sheetId, range)
                .execute()
                .getValues();
    }
}
