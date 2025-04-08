package sad.test.pdfscan.integration;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CheckSpecifications;
import sad.test.pdfscan.config.CountriesSpecificationProperties;
import sad.test.pdfscan.config.DefaultSpecificationProperties;
import sad.test.pdfscan.model.CheckElement;
import sad.test.pdfscan.services.PdfServiceImpl;
import sad.test.pdfscan.utils.Constants;
import sad.test.pdfscan.utils.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class PdfServiceIntegrationTest {

    @Autowired
    PdfServiceImpl pdfService;

    @Autowired
    private BlackListedProperties blackListedProperties;

    @Autowired
    private CountriesSpecificationProperties countriesSpecificationProperties;

    @Autowired
    private DefaultSpecificationProperties defaultSpecificationProperties;

    @Autowired
    private CheckSpecifications checkSpecifications;

    public List<CheckElement> loadListElement(){
        return this.checkSpecifications.getElements().stream().filter(checkElement -> checkSpecifications.getActiveElementsToCheck().contains(checkElement.getName())).collect(Collectors.toList());
    }
    
    @Test
    void downloadAndStorePdfTest(){
        String fileUrl = StringUtils.generateFileName(Constants.FILE_URL);
        Path path = Paths.get(fileUrl);
        Truth.assertThat(Files.exists(path)).isFalse();
        ResponseEntity response = pdfService.downloadAndStorePdf("DE",Constants.PDF_TEST_LINK, fileUrl);
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
        ResponseEntity response = pdfService.downloadAndStorePdf("DE",url, url);
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
        ResponseEntity response = pdfService.downloadAndStorePdf("DE",Constants.PDF_TEST_LINK_TXT, fileUrl);
        Truth.assertThat(response.getStatusCode().isError()).isTrue();
        Truth.assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        Truth.assertThat(Files.exists(path)).isFalse();
    }

    @Test
    void checkBlacklistedElementsTest(){
        ResponseEntity response = pdfService.checkSpecifications(
                null,
                Constants.FILE_URL + "Testdata_Invoices.pdf",
                defaultSpecificationProperties,
                blackListedProperties,
                countriesSpecificationProperties,
                loadListElement());

        Truth.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Truth.assertThat(response.getBody().toString().contains("do not match")).isTrue();
    }

    @Test
    void checkBlacklistedIbanExistTest(){
        List<CheckElement> checkElementList = loadListElement();
        checkElementList.stream().filter(checkElement -> checkElement.getName().equalsIgnoreCase("IBAN"))
                .findFirst().get().getBlacklisted().add("DE15 3006 0601 0505 7807 80");
        ResponseEntity response = pdfService.checkSpecifications(
                null,
                Constants.FILE_URL + "Testdata_Invoices.pdf",
                defaultSpecificationProperties,
                blackListedProperties,
                countriesSpecificationProperties,
                checkElementList);

        Truth.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void checkBlacklistedElementNoMatchSpecTest(){
        defaultSpecificationProperties.setCountry("FR");
        ResponseEntity response = pdfService.checkSpecifications(
                null,
                Constants.FILE_URL + "Testdata_Invoices.pdf",
                defaultSpecificationProperties,
                blackListedProperties,
                countriesSpecificationProperties,
                loadListElement());

        Truth.assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Truth.assertThat(response.getBody().toString().contains("do not match the IBAN specification")).isTrue();
    }

    @Test
    void checkBlacklistedElementInvalidUrlTest(){
        ResponseEntity response = pdfService.checkSpecifications(
                null,
                "test.pdf",
                defaultSpecificationProperties,
                blackListedProperties,
                countriesSpecificationProperties,
                loadListElement());

        Truth.assertThat(response.getStatusCode().is4xxClientError()).isTrue();

        response = pdfService.checkSpecifications(
                null,
                Constants.FILE_URL + "test.pdf",
                defaultSpecificationProperties,
                blackListedProperties,
                countriesSpecificationProperties,
                loadListElement());
        Truth.assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

}
