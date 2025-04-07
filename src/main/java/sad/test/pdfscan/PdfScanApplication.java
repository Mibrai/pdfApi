package sad.test.pdfscan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@SpringBootApplication
public class PdfScanApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfScanApplication.class, args);
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages/messages");
        messageSource.setDefaultLocale(new Locale("en","en"));
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
