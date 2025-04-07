package sad.test.pdfscan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesSpecificationProperties;
import sad.test.pdfscan.config.DefaultSpecificationProperties;
import sad.test.pdfscan.model.CheckElement;
import sad.test.pdfscan.services.PdfServiceImpl;
import sad.test.pdfscan.utils.Constants;
import sad.test.pdfscan.utils.StringUtils;

import java.util.List;

@RestController
public class MainController {

    @Autowired
    PdfServiceImpl pdfService;

    @PostMapping("/checkBlacklistedElements")
    private ResponseEntity processUrl(
            @RequestParam(name = "pdfDownloadUrl") String url,
            @RequestParam(name = "countryCode", required = false) String countryCode,
            final ModelMap modelMap) throws InterruptedException {

         String currentStoreUrl = StringUtils.generateFileName(Constants.FILE_URL);

         String checkedUrl = StringUtils.validUrl(url);

         if(checkedUrl == null){
             return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Invalid URL : " + checkedUrl);
         }

        ResponseEntity response =  pdfService.downloadAndStorePdf(countryCode,checkedUrl, currentStoreUrl);

        if(response.getStatusCode().is2xxSuccessful()) {
            Thread.sleep(2000);
            response = pdfService.checkSpecifications(countryCode,
                    currentStoreUrl,
                    (DefaultSpecificationProperties) modelMap.getAttribute("defaultProperties"),
                    (BlackListedProperties) modelMap.getAttribute("blackListedIbanOrCountries"),
                    (CountriesSpecificationProperties) modelMap.getAttribute("countriesSpecProperties"),
                    (List<CheckElement>) modelMap.getAttribute("currentCheckSpecifications"));

        }
        pdfService.deleteFile(currentStoreUrl);
        return response;
    }
}
