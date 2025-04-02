package sad.test.pdfscan.services;

import org.springframework.scheduling.annotation.Async;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;

public interface PdfService {

    boolean pdfValid(String url);
    boolean pdfExist(String url, final DefaultIbanProperties defaultIbanProperties, final BlackListedProperties blackListedProperties);

    @Async
    boolean getPdfFile(String url, String store);

}
