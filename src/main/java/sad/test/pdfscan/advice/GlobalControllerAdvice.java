package sad.test.pdfscan.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sad.test.pdfscan.config.*;
import sad.test.pdfscan.model.CheckElement;

import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalControllerAdvice {

    private final Config config;

    @Autowired
    private BlackListedProperties blackListedProperties;

    @Autowired
    private CountriesSpecificationProperties countriesSpecificationProperties;

    @Autowired
    private DefaultSpecificationProperties defaultSpecificationProperties;

    @Autowired
    private CheckSpecifications checkSpecifications;
    public GlobalControllerAdvice(Config config) {
        this.config = config;
    }

    @ModelAttribute(name = "defaultProperties")
    public DefaultSpecificationProperties getDefaultProperties(){
        return config.getIbanProperties(defaultSpecificationProperties, countriesSpecificationProperties);
    }

    @ModelAttribute(name = "blackListedIbanOrCountries")
    public BlackListedProperties getBlackListedProperties(){
        return blackListedProperties;
    }

    @ModelAttribute(name = "countriesSpecProperties")
    public CountriesSpecificationProperties getCountriesSpecProperties(){
        return countriesSpecificationProperties;
    }

    @ModelAttribute(name = "checkSpecifications")
    public CheckSpecifications getCheckSpecifications(){return checkSpecifications;}

    @ModelAttribute(name = "currentCheckSpecifications")
    public List<CheckElement> getCurrentCheckElements(){
        return checkSpecifications.getElements().stream().filter(checkElement -> checkSpecifications.getActiveElementsToCheck().contains(checkElement.getName())).collect(Collectors.toList());
    }
}
