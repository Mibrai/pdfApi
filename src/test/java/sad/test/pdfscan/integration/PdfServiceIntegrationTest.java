package sad.test.pdfscan.integration;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesIbanProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;
import sad.test.pdfscan.services.PdfServiceImpl;
import sad.test.pdfscan.utils.Constants;
import sad.test.pdfscan.utils.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
public class PdfServiceIntegrationTest {

    @Autowired
    PdfServiceImpl pdfService;

    @Autowired
    private BlackListedProperties blackListedProperties;

    @Autowired
    private CountriesIbanProperties countriesIbanProperties;

    @Autowired
    private DefaultIbanProperties defaultIbanProperties;
    
    @Test
    void downloadAndStorePdfTest(){
        String fileUrl = StringUtils.generateFileName(Constants.FILE_URL);
        Path path = Paths.get(fileUrl);
        Truth.assertThat(Files.exists(path)).isFalse();
        ResponseEntity response = pdfService.downloadAndStorePdf(Constants.PDF_TEST_LINK, fileUrl);
        Truth.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Truth.assertThat(Files.exists(path)).isTrue();
        pdfService.deleteFile(fileUrl);
        Truth.assertThat(Files.exists(path)).isFalse();
    }

    @Test
    void downloadAndStorePdfTestFailure(){
        String url = "static/test.pdf";
        Path path = Paths.get(url);
        Truth.assertThat(Files.exists(path)).isFalse();
        ResponseEntity response = pdfService.downloadAndStorePdf(url, url);
        Truth.assertThat(response.getStatusCode().isError()).isTrue();
        Truth.assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        Truth.assertThat(response.getBody().toString().contains("no protocol")).isTrue();
        Truth.assertThat(Files.exists(path)).isFalse();
    }

    @Test
    void downloadAndStorePdfTestNotPdf(){
        String fileUrl = StringUtils.generateFileName(Constants.FILE_URL);
        Path path = Paths.get(fileUrl);
        Truth.assertThat(Files.exists(path)).isFalse();
        ResponseEntity response = pdfService.downloadAndStorePdf(Constants.PDF_TEST_LINK_TXT, fileUrl);
        Truth.assertThat(response.getStatusCode().isError()).isTrue();
        Truth.assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        Truth.assertThat(response.getBody().toString().contains("Valid PDF file not found")).isTrue();
        Truth.assertThat(Files.exists(path)).isFalse();
    }

    @Test
    void checkBlacklistedIbanTest(){
        ResponseEntity response = pdfService.checkBlacklistedIban(
                null,
                Constants.FILE_URL + "Testdata_Invoices.pdf",
                defaultIbanProperties,
                blackListedProperties,
                countriesIbanProperties);

        Truth.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Truth.assertThat(response.getBody().toString().contains("No blacklisted Iban found")).isTrue();
    }

    @Test
    void checkBlacklistedIbanExistTest(){
        blackListedProperties.getIbans().add("DE15 3006 0601 0505 7807 80");
        ResponseEntity response = pdfService.checkBlacklistedIban(
                null,
                Constants.FILE_URL + "Testdata_Invoices.pdf",
                defaultIbanProperties,
                blackListedProperties,
                countriesIbanProperties);

        Truth.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Truth.assertThat(response.getBody().toString().contains("is blacklisted")).isTrue();
    }

    @Test
    void checkBlacklistedIbanNoMatchSpecTest(){
        defaultIbanProperties.setCountry("FR");
        ResponseEntity response = pdfService.checkBlacklistedIban(
                null,
                Constants.FILE_URL + "Testdata_Invoices.pdf",
                defaultIbanProperties,
                blackListedProperties,
                countriesIbanProperties);

        Truth.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Truth.assertThat(response.getBody().toString().contains("don't match the IBAN specification")).isTrue();
    }

    @Test
    void checkBlacklistedIbanInvalidUrlTest(){
        ResponseEntity response = pdfService.checkBlacklistedIban(
                null,
                "test.pdf",
                defaultIbanProperties,
                blackListedProperties,
                countriesIbanProperties);

        Truth.assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        Truth.assertThat(response.getBody().toString().contains("File not found")).isTrue();

        response = pdfService.checkBlacklistedIban(
                null,
                Constants.FILE_URL + "test.pdf",
                defaultIbanProperties,
                blackListedProperties,
                countriesIbanProperties);
        Truth.assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        Truth.assertThat(response.getBody().toString().contains("File not found")).isTrue();
    }

}
