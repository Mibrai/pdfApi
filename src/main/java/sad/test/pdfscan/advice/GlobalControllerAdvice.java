package sad.test.pdfscan.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.Config;
import sad.test.pdfscan.config.CountriesIbanProperties;
import sad.test.pdfscan.config.DefaultIbanProperties;


@ControllerAdvice
public class GlobalControllerAdvice {

    private final Config config;

    @Autowired
    private BlackListedProperties blackListedProperties;

    @Autowired
    private CountriesIbanProperties countriesIbanProperties;

    @Autowired
    private DefaultIbanProperties defaultIbanProperties;
    public GlobalControllerAdvice(Config config) {
        this.config = config;
    }

    @ModelAttribute(name = "ibanProperties")
    public DefaultIbanProperties getDefaultIbanProperties(){
        return config.getIbanProperties(defaultIbanProperties,countriesIbanProperties);
    }

    @ModelAttribute(name = "blackListedIbanOrCountries")
    public BlackListedProperties getBlackListedProperties(){
        return blackListedProperties;
    }

    @ModelAttribute(name = "countriesIbanProperties")
    public CountriesIbanProperties getCountriesIbanProperties(){
        return countriesIbanProperties;
    }

}
