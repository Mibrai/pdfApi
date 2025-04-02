package sad.test.pdfscan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesIbanProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;
import sad.test.pdfscan.model.Country;
import sad.test.pdfscan.services.PdfServiceImpl;

import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    public static String FILE_URL = "C:\\Users\\paric\\Downloads\\PROJEKT\\Programmierungskurs\\pdfScan\\src\\main\\resources\\static\\test.pdf";

    @Autowired
    DefaultIbanProperties config;

    @Autowired
    CountriesIbanProperties countriesIbanProperties;

    @Autowired
    BlackListedProperties blackListedProperties;

    @Autowired
    DefaultIbanProperties defaultIbanProperties;

    @Autowired
    PdfServiceImpl pdfService;

    @GetMapping(value = "/home")
    private String loadHome(
    ) throws InterruptedException {

        pdfService.getPdfFile("https://room4-solutions.com/testApi/Testdata_Invoices.pdf",
                FILE_URL);


        Thread.sleep(2000);
        pdfService.pdfExist(FILE_URL, defaultIbanProperties, blackListedProperties);
        return "home";
    }

    @PostMapping("/scanUrl")
    private String processUrl(@RequestBody String url){

        return "Response : " +url;
    }
}
