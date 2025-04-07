package sad.test.pdfscan.services;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.commons.io.FileDeleteStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesIbanProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;
import sad.test.pdfscan.utils.Constants;
import sad.test.pdfscan.utils.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

@Service
public class PdfServiceImpl implements  PdfService{

    /**
     * Take a valid url of a Pdf and read all text of all page to find IBAN
     * For each page when IBAN is found , the extraction of exact EBAN value begin
     * In our case the IBAN value is between the botch String ,,IBAN:'' and ,,SWIFT''
     * the exact Iban will be check if it matchs the Country Specification (Code, Size, withWitheSpace between or not)
     * The differents Specifications are in our Config file (application.yml)
     * @param url
     * @param defaultIbanProperties
     * @param blackListedProperties
     * @return
     */
    @Override
    public ResponseEntity checkBlacklistedIban(
            final String countryCode,
            final String url,
            final DefaultIbanProperties defaultIbanProperties,
            final BlackListedProperties blackListedProperties,
            final CountriesIbanProperties countriesIbanProperties) {

        StringBuilder state = new StringBuilder();
        try{
            Thread.sleep(1000);
            PdfReader pdfReader = new PdfReader(url);
            int numberOfPage = pdfReader.getNumberOfPages();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i <= numberOfPage; i++){
                String pageText = PdfTextExtractor.getTextFromPage(pdfReader,i);
                if(pageText.contains(Constants.IBAN)){
                    int initPositionIban = pageText.indexOf(Constants.IBAN);

                    if(initPositionIban != -1){
                        int endPositionIban = pageText.indexOf(Constants.SWIFT);
                        String iban = pageText.substring(initPositionIban,endPositionIban).trim();
                        String extractedIban = StringUtils.extractIban(iban);
                        boolean matchSpec = StringUtils.ibanMatchCountrySpec(countryCode,
                                defaultIbanProperties,countriesIbanProperties,extractedIban);
                        if(matchSpec){
                            if(StringUtils.isBlackListed(extractedIban,blackListedProperties))
                                if(!stringBuilder.toString().contains(extractedIban))
                                    stringBuilder.append("IBAN: " + extractedIban + " , is blacklisted : true \n\n" );
                        } else {
                            state.append("IBAN: " + extractedIban + " don't match the IBAN specification for current country");
                        }
                    }
                }
            }

            if(stringBuilder.isEmpty()){
                return ResponseEntity.ok(state.isEmpty() ? "No blacklisted Iban found" : state);
            } else {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(stringBuilder);
            }
        } catch (IOException ex){
            state.append("File not found  : "+ex.getMessage());
        } catch (InterruptedException e) {
            state.append("\n" + "Interruption Exception + " + e.getMessage());
        }
        return ResponseEntity.badRequest().body(state);
    }

    /**
     * Download pdf and store in local Storage
     * @param url
     * @param store
     * @return
     */
    @Async
    @Override
    public ResponseEntity downloadAndStorePdf(String url, String store) {
            StringBuilder state = new StringBuilder();
        try{
            URL pdfUrl = new URL(url);
            URLConnection urlConnection = pdfUrl.openConnection();
            if(urlConnection.getContentType().equalsIgnoreCase("application/pdf")){
                FileOutputStream fileOutputStream = new FileOutputStream(store);

                byte[] byteArray = new byte[1024];
                int readLength;

                InputStream inputStream = pdfUrl.openStream();
                while ((readLength = inputStream.read(byteArray)) > 0){
                    fileOutputStream.write(byteArray, 0, readLength);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                return ResponseEntity.ok("Pdf downloaded and stored");
            } else {
                state.append("Valid PDF file not found at the given Adress : "+ pdfUrl);
            }

        } catch(FileNotFoundException ex){
            state.append("File not foud at : " + store + " \n msg : "+ex.getMessage());
        } catch (IOException ex){
            state.append("IO Exeption : "+ ex.getMessage());
        } catch (Exception ex){
            state.append("Can't reach pdf file : "+ ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(state);
    }

    /**
     * remove file after usage to make space free
     * @param url
     * @return
     */
    @Override
    public boolean deleteFile(String url) {
        System.gc();
       return FileDeleteStrategy.FORCE.deleteQuietly(new File(url));
    }
}
