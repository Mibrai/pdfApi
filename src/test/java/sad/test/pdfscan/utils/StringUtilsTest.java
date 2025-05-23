package sad.test.pdfscan.utils;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CheckSpecifications;
import sad.test.pdfscan.config.CountriesSpecificationProperties;
import sad.test.pdfscan.config.DefaultSpecificationProperties;
import sad.test.pdfscan.model.CheckElement;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class StringUtilsTest {

    @Autowired
    DefaultSpecificationProperties defaultSpecificationProperties;

    @Autowired
    CountriesSpecificationProperties countriesSpecificationProperties;

    @Autowired
    BlackListedProperties blackListedProperties;

    @Autowired
    private CheckSpecifications checkSpecifications;

    private static final String IBAN = "DE27 9837 7376 6664 5553 50";
    private static final String ALLOWED_IBAN = "DE27 9837 7376 6664 5553 60";


    public List<CheckElement> loadListElement(){
        return this.checkSpecifications.getElements().stream().filter(checkElement -> checkSpecifications.getActiveElementsToCheck().contains(checkElement.getName())).collect(Collectors.toList());
    }
    @Test
    void elementMatchCountrySpecTest(){
        CheckElement checkElement = CheckElement.builder()
                .name("IBAN")
                .lastString("SWIFT")
                .withWhiteSpace(false)
                .minSize(22)
                .maxSize(22)
                .initialString("IBAN:")
                .build();
        //test empty country code
        Truth.assertThat(StringUtils.elementMatchCountrySpec("", defaultSpecificationProperties, countriesSpecificationProperties,"",checkElement)).isFalse();
        //test with null defaultIbanProperties and null countriesIbanProperties
        Truth.assertThat(StringUtils.elementMatchCountrySpec("",null, countriesSpecificationProperties,"",checkElement)).isFalse();
        Truth.assertThat(StringUtils.elementMatchCountrySpec("DE",null,null,IBAN,checkElement)).isFalse();
        Truth.assertThat(StringUtils.elementMatchCountrySpec(null, defaultSpecificationProperties,null,IBAN,checkElement)).isFalse();
        Truth.assertThat(StringUtils.elementMatchCountrySpec(null,null,null,IBAN,checkElement)).isFalse();
        //match country code and country size
        Truth.assertThat(StringUtils.elementMatchCountrySpec("DE", defaultSpecificationProperties, countriesSpecificationProperties,IBAN,checkElement)).isTrue();
        //don't match country code
        Truth.assertThat(StringUtils.elementMatchCountrySpec("DE", defaultSpecificationProperties, countriesSpecificationProperties,IBAN.replaceAll("DE","FR"),checkElement)).isFalse();
        //match country code but don't match country size
        Truth.assertThat(StringUtils.elementMatchCountrySpec("FR", defaultSpecificationProperties, countriesSpecificationProperties,IBAN.replaceAll("DE","FR"),checkElement)).isFalse();
        //match country code and country size
        Truth.assertThat(StringUtils.elementMatchCountrySpec("FR", defaultSpecificationProperties, countriesSpecificationProperties,IBAN.replaceAll("DE","FR").substring(0,18),checkElement)).isTrue();
    }

    @Test
    void extractElementTest(){
        String initialString = "IBAN:";
        StringBuilder stringBuilder = new StringBuilder("IBAN:");
        Truth.assertThat(StringUtils.extractElementInfos("",initialString).isBlank()).isTrue();
        Truth.assertThat(StringUtils.extractElementInfos(null,initialString) == null).isTrue();
        Truth.assertThat(StringUtils.extractElementInfos(stringBuilder.append(IBAN).toString(),initialString).contains("IBAN:")).isFalse();
    }

    @Test
    void isBlacklistedTest(){
        Truth.assertThat(StringUtils.isBlackListed("DE",IBAN,blackListedProperties,loadListElement().get(0))).isTrue();
        Truth.assertThat(StringUtils.isBlackListed("DE",ALLOWED_IBAN,blackListedProperties,loadListElement().get(0))).isFalse();
        blackListedProperties.getCountries().add("DE");
        Truth.assertThat(StringUtils.isBlackListed("DE",ALLOWED_IBAN,blackListedProperties,loadListElement().get(0))).isTrue();
        Truth.assertThat(StringUtils.isBlackListed("DE",null,blackListedProperties,loadListElement().get(0))).isFalse();
        Truth.assertThat(StringUtils.isBlackListed("DE","12345",blackListedProperties,loadListElement().get(0))).isTrue();
    }

    @Test
    void validUrlTest(){
        StringBuilder urlBuilder = new StringBuilder("test.de");
        Truth.assertThat(StringUtils.validUrl(urlBuilder.toString()).contains("https")).isTrue();
        urlBuilder.insert(0,"http://");
        Truth.assertThat(StringUtils.validUrl(urlBuilder.toString()).contains("https")).isFalse();
        Truth.assertThat(StringUtils.validUrl("test.de.https://")).isNull();
        Truth.assertThat(StringUtils.validUrl("testhttp://test.de")).isNull();
    }
}
