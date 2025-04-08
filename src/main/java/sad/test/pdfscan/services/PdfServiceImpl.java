package sad.test.pdfscan.services;

import com.itextpdf.text.pdf.PdfReader;
import org.apache.commons.io.FileDeleteStrategy;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesSpecificationProperties;
import sad.test.pdfscan.config.DefaultSpecificationProperties;
import sad.test.pdfscan.model.CheckElement;
import sad.test.pdfscan.utils.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

@Service
public class PdfServiceImpl implements  PdfService{

    private final MessageSource messageSource;

    public PdfServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Take a valid url of a Pdf and read all text of all page to find IBAN
     * For each page when IBAN is found , the extraction of exact EBAN value begin
     * In our case the IBAN value is between the botch String ,,IBAN:'' and ,,SWIFT''
     * the exact Iban will be check if it matchs the Country Specification (Code, Size, withWitheSpace between or not)
     * The differents Specifications are in our Config file (application.yml)
     * @param url
     * @param defaultSpecificationProperties
     * @param blackListedProperties
     * @return
     */
    @Override
    public ResponseEntity checkSpecifications(
            final String countryCode,
            final String url,
            final DefaultSpecificationProperties defaultSpecificationProperties,
            final BlackListedProperties blackListedProperties,
            final CountriesSpecificationProperties countriesSpecificationProperties,
            final List<CheckElement> checkElementList) {

        StringBuilder state = new StringBuilder();
        String country = countryCode == null ? "EN" : countryCode;
        Locale locale = new Locale(country.toLowerCase(), country.toLowerCase());
        try{
            Thread.sleep(1000);
            PdfReader pdfReader = new PdfReader(url);
            List<String> pdfStrings = StringUtils.getAllPdfTextAsList(pdfReader);
            pdfReader.close();
                for (String pageText : pdfStrings){
                        //iterate the checkElementList here
                        checkElementList.stream().forEach(checkElement -> {
                            if(pageText != null && pageText.contains(checkElement.getInitialString())){
                                int initStringPosition = pageText.indexOf(checkElement.getInitialString());

                                if(initStringPosition != -1){
                                    int endStringPosition = checkElement.getLastString().equalsIgnoreCase("\\n") ? (int)(initStringPosition + (checkElement.getInitialString().length() + 1 ) + (checkElement.getMaxSize() + 1)) : pageText.indexOf(checkElement.getLastString());
                                    String element = pageText.substring(initStringPosition,endStringPosition).trim();
                                    String extractedElementInfo = StringUtils.extractElementInfos(element,checkElement.getInitialString());
                                    boolean matchSpec = StringUtils.elementMatchCountrySpec(
                                            countryCode,
                                            defaultSpecificationProperties,
                                            countriesSpecificationProperties,
                                            extractedElementInfo,
                                            checkElement);

                                    if(matchSpec){
                                        if(StringUtils.isBlackListed(countryCode,extractedElementInfo,blackListedProperties,checkElement))
                                            if(!state.toString().contains(extractedElementInfo))
                                                state.append(messageSource.getMessage("log.blacklisted",
                                                        new Object[]{checkElement.getName() + ": ",extractedElementInfo},
                                                        locale));
                                    } else {
                                        if(!state.toString().contains(extractedElementInfo))
                                            state.append(messageSource.getMessage("log.nomatch",
                                                    new Object[]{extractedElementInfo,checkElement.getName(),checkElement.getName(),
                                                            StringUtils.getSpecMessage(
                                                            countryCode,
                                                            defaultSpecificationProperties,
                                                            countriesSpecificationProperties,
                                                            checkElement,
                                                            messageSource,
                                                            locale
                                                    )},
                                                    locale));
                                    }
                                }
                            }
                        });
                       //End forEach
                }

            if(state.isEmpty()){
                return ResponseEntity.ok(state.isEmpty() ? messageSource.getMessage("log.notfound",null,locale) : state);
            } else {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(state);
            }
        }catch (IOException e) {
            state.append(messageSource.getMessage("log.file.notfound",
                    new Object[]{e.getMessage()},
                    locale));
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
    public ResponseEntity downloadAndStorePdf(final String countryCode ,final String url,final String store) {
            StringBuilder state = new StringBuilder();
        Locale locale = new Locale(countryCode.toLowerCase(), countryCode.toLowerCase());
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
                state.append(messageSource.getMessage("log.file.invalid",
                        new Object[]{pdfUrl},
                        locale));
            }

        } catch(FileNotFoundException ex){
            state.append(messageSource.getMessage("log.file.notfound",
                    new Object[]{store},
                    locale));
        } catch (IOException ex){
            state.append("IO Exeption : "+ ex.getMessage());
        } catch (Exception ex){
            state.append(messageSource.getMessage("log.file.noaccess",
                    new Object[]{ex.getMessage()},
                    locale));
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
