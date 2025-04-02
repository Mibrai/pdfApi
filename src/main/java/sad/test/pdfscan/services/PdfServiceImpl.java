package sad.test.pdfscan.services;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;
import sad.test.pdfscan.utils.StringUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Service
public class PdfServiceImpl implements  PdfService{
    @Override
    public boolean pdfValid(String url) {

        return false;
    }

    @Override
    public boolean pdfExist(String url,
                            final DefaultIbanProperties defaultIbanProperties,
                            final BlackListedProperties blackListedProperties) {
        try{
            Thread.sleep(1000);
            PdfReader pdfReader = new PdfReader(url);
            int numberOfPage = pdfReader.getNumberOfPages();
            System.out.println("Number of Page : " + numberOfPage);

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i <= numberOfPage; i++){
                String pageText = PdfTextExtractor.getTextFromPage(pdfReader,i);
                if(StringUtils.textContainIban(pageText,"IBAN")){
                    int initPositionIban = StringUtils.getInitIndexOfString(pageText,"IBAN");
                    int endPositionIban = StringUtils.getInitIndexOfString(pageText,"SWIFT");
                    String iban = pageText.substring(initPositionIban,endPositionIban).trim();
                    String extractedIban = StringUtils.extractIban(iban);
                    boolean matchSpec = StringUtils.ibanMatchCountrySpec(defaultIbanProperties,extractedIban);
                    if(matchSpec){
                        System.out.println("Iban " + iban + " , isBlacklisted : " + StringUtils.isBlackListed(extractedIban,blackListedProperties) );
                    }
                    stringBuilder.append(iban);
                    stringBuilder.append("\n\n");
                }

            }
            //System.out.println("PDF Content :  \n\n "+stringBuilder);
        } catch (IOException ex){
            System.out.println("File not found  : "+ex.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Async
    @Override
    public boolean getPdfFile(String url, String store) {

        try{
            URL pdfUrl = new URL(url);
            URLConnection urlConnection = pdfUrl.openConnection();
            if(urlConnection.getContentType().equalsIgnoreCase("application/pdf")){
                FileOutputStream fileOutputStream = new FileOutputStream(store);

                byte[] byteArray = new byte[1024]; // amount of bytes reading from input stream at a given time
                int readLength;

                InputStream inputStream = pdfUrl.openStream();
                while ((readLength = inputStream.read(byteArray)) > 0){
                    fileOutputStream.write(byteArray, 0, readLength);
                }

                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
            }

        } catch(FileNotFoundException ex){
            System.out.println("File not foud at : " + store + " \n msg : "+ex.getMessage());
        } catch (IOException ex){
            System.out.println("IO Exeption : "+ ex.getMessage());
        }catch (Exception ex){
            System.out.println("File Exception : "+ex.getMessage());
        }

        return false;
    }
}
