package sad.test.pdfscan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesIbanProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;
import sad.test.pdfscan.services.PdfServiceImpl;
import sad.test.pdfscan.utils.Constants;
import sad.test.pdfscan.utils.StringUtils;

@RestController
public class MainController {

    @Autowired
    PdfServiceImpl pdfService;

    @GetMapping(value = "/home")
    private ResponseEntity loadHome(
            final ModelMap modelMap
            ) throws InterruptedException {

       /* pdfService.downloadAndStorePdf(Constants.PDF_TEST_LINK,
                Constants.FILE_URL);


        Thread.sleep(2000);
        return  pdfService.checkBlacklistedIban(Constants.FILE_URL,
                (DefaultIbanProperties) modelMap.getAttribute("ibanProperties"),
                (BlackListedProperties) modelMap.getAttribute("blackListedIbanOrCountries"));*/
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Boommm");
    }

    @PostMapping("/checkBlacklistedIban")
    private ResponseEntity processUrl(
            @RequestParam(name = "pdfUrl") String url,
            @RequestParam(name = "ibanCountryCode", required = false) String countryCode,
            final ModelMap modelMap) throws InterruptedException {

         String currentStoreUrl = StringUtils.generateFileName(Constants.FILE_URL);

         String checkedUrl = StringUtils.validUrl(url);

         if(checkedUrl == null){
             return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid URL : " + checkedUrl);
         }

        ResponseEntity response =  pdfService.downloadAndStorePdf(checkedUrl, currentStoreUrl);
        if(response.getStatusCode().is2xxSuccessful()) {
            Thread.sleep(2000);
            response = pdfService.checkBlacklistedIban(countryCode,
                    currentStoreUrl,
                    (DefaultIbanProperties) modelMap.getAttribute("ibanProperties"),
                    (BlackListedProperties) modelMap.getAttribute("blackListedIbanOrCountries"),
                    (CountriesIbanProperties) modelMap.getAttribute("countriesIbanProperties"));

        }
        pdfService.deleteFile(currentStoreUrl);
        return response;
    }
}
