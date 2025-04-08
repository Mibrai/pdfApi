package sad.test.pdfscan.utils;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.springframework.context.MessageSource;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesSpecificationProperties;
import sad.test.pdfscan.config.DefaultSpecificationProperties;
import sad.test.pdfscan.model.CheckElement;
import sad.test.pdfscan.model.Country;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.*;

public class StringUtils {
    /**
     * Check if any Country is given, when not the default Country-Spec will be used
     * The default-size and default-withWhiteSpace are used only when the default-Contry Code not exist in the countries-iban-properties
     * @param countryCode
     * @param defaultSpecificationProperties
     * @param countriesSpecificationProperties
     * @param element
     * @return boolean true if the given Iban matchs the Specifications of the given Country or DefaultCountry
     */
    public static boolean elementMatchCountrySpec(final String countryCode,
                                                  final DefaultSpecificationProperties defaultSpecificationProperties,
                                                  final CountriesSpecificationProperties countriesSpecificationProperties,
                                                  final String element,
                                                  final CheckElement checkElement){
        if (countryCode != null && !countryCode.isBlank() && countriesSpecificationProperties != null) {
            Optional<Country> country = countriesSpecificationProperties.getCountry().stream()
                    .filter(country1 -> country1.getCode().equalsIgnoreCase(countryCode)).findFirst();
            if (country.isEmpty())
                return false;

            defaultSpecificationProperties.setCountry(country.get().getCode());
            defaultSpecificationProperties.setSize(country.get().getSize());
            defaultSpecificationProperties.setWithWhiteSpace(country.get().isWithWhiteSpace());
        }
        return matchSpec(defaultSpecificationProperties, element, checkElement);

    }

    /**
     * Check if element contains the Country-Code
     * Check if by size-verification the whiteSpace into Iban must be count or not
     * @param defaultSpecificationProperties
     * @param element
     * @return
     */
    private static boolean matchSpec(final DefaultSpecificationProperties defaultSpecificationProperties,
                                     final String element,
                                     final CheckElement checkElement){

        switch (checkElement.getName()){
            case Constants.IBAN :
                if(defaultSpecificationProperties != null &&
                        element != null && !element.isBlank()){
                    if(defaultSpecificationProperties.getCountry().equalsIgnoreCase(element.substring(0,2)))
                        if(defaultSpecificationProperties.isWithWhiteSpace()){
                            return element.length() == defaultSpecificationProperties.getSize();
                        } else {
                            return element.replaceAll(" ","").length() == defaultSpecificationProperties.getSize();
                        }
                }
                break;
           /* case Constants.BIC:
                if(checkElement.getMinSize() != 0 && checkElement.getMaxSize() != 0)
                    return element.length() <= checkElement.getMaxSize();
                another test can be specified  here as you like: e.g : BIC, KONTO, etc.. */
            default:
                return element.length() >= checkElement.getMinSize() && element.length() <= checkElement.getMaxSize();
        }
        return false;
    }

    /**
     * Extract the exact Iban value
     * @param pageText
     * @return
     */
    public static String extractElementInfos(final String pageText, final String initialString){
        if(pageText != null && pageText.contains(initialString)){
            // e.g :  IBAN:  DE15 3006 0601 0505 7807 80
            String[] ibanArray = pageText.split(":");
            if(ibanArray.length == 2){
                return ibanArray[1].trim();
            }
        }
        return pageText;
    }

    /**
     * We have defined two cases in which the item is blacklisted: through the country of origin or the fixed blacklist.
     * we check if the given element are in the both case
     * @param element
     * @param blackListedProperties
     * @return
     */
    public static boolean isBlackListed(final String countryCode,
                                        final String element,
                                        final BlackListedProperties blackListedProperties,
                                        final CheckElement checkElement){
        boolean state = false;

        if(element == null || element.isBlank())
            return false;

        String blockElement = element.trim().replaceAll(" ","");

        if(checkElement.getBlacklisted().contains(blockElement) ||
                checkElement.getBlacklisted().contains(element.trim()))
            state = true;

        if(countryCode != null && blackListedProperties.getCountries().contains(countryCode.trim().toUpperCase()))
            state = true;

        return state;
    }

    /**
     * Generate unique filename
     * @param storeUrl
     * @return
     */
    public static String generateFileName(String storeUrl){
        UUID uuid = UUID.randomUUID();
        return storeUrl+"\\file"+uuid+".pdf";
    }

    /**
     * check if given url are valid
     * set url when not valid
     * @param url
     * @return
     */
    public static String validUrl(String url){
        StringBuilder stringBuilder = new StringBuilder();
        if(!url.contains(Constants.HTTP) && !url.contains(Constants.HTTPS)){
            stringBuilder.append(Constants.HTTPS);
        }

        if(url.contains(Constants.HTTPS)
                && url.indexOf(Constants.HTTPS) != 0  ){
            return null;
        }

        if(url.contains(Constants.HTTP)
                && url.indexOf(Constants.HTTP) != 0  ){
            return null;
        }
        stringBuilder.append(url);
        return stringBuilder.toString();
    }

    /**
     * Read PDF file and retun all pages in List-Items form
     * @param pdfReader
     * @return
     */
    public static List<String> getAllPdfTextAsList(final PdfReader pdfReader){
        List<String> pdfTexts = new ArrayList<>();
        try {
            int numberOfPage = pdfReader.getNumberOfPages();
            for (int i = 1; i <= numberOfPage; i++ ){
                pdfTexts.add(PdfTextExtractor.getTextFromPage(pdfReader,i));
            }
        } catch (IOException e) {
            //
        }

        return pdfTexts;
    }

    /**
     * Concatenate valid Message to show when the api is called
     * @param countryCode
     * @param defaultSpecificationProperties
     * @param countriesSpecificationProperties
     * @param checkElement
     * @param messageSource
     * @param locale
     * @return
     */
    public static String getSpecMessage(final String countryCode, final DefaultSpecificationProperties defaultSpecificationProperties,
                                        final CountriesSpecificationProperties countriesSpecificationProperties,
                                        final CheckElement checkElement,
                                        final MessageSource messageSource,
                                        final Locale locale){
        StringBuilder messageBuilder = new StringBuilder();
        switch (checkElement.getName()){
            case Constants.IBAN :
                if (countryCode != null && !countryCode.isBlank() && countriesSpecificationProperties != null) {
                    Optional<Country> country = countriesSpecificationProperties.getCountry().stream()
                            .filter(country1 -> country1.getCode().equalsIgnoreCase(countryCode)).findFirst();
                    if (!country.isEmpty()){
                        messageBuilder.append(messageSource.getMessage("log.spec.message.country",new Object[]{country.get().getName()},locale));
                        messageBuilder.append("Code : "+country.get().getCode()+"\n");
                        messageBuilder.append(messageSource.getMessage("log.spec.message.iban.length",new Object[]{country.get().getSize()},locale));
                        messageBuilder.append(messageSource.getMessage("log.spec.message.whitespace",new Object[]{country.get().isWithWhiteSpace()},locale));
                        messageBuilder.append(Constants.LINE);
                    }  else {
                        messageBuilder.append("Country Code : "+defaultSpecificationProperties.getCountry()+"\n");
                        messageBuilder.append(messageSource.getMessage("log.spec.message.iban.length",new Object[]{defaultSpecificationProperties.getSize()},locale));
                        messageBuilder.append(messageSource.getMessage("log.spec.message.whitespace",new Object[]{defaultSpecificationProperties.isWithWhiteSpace()},locale));
                        messageBuilder.append(Constants.LINE);
                    }
                }
                break;
            default:
                messageBuilder.append(messageSource.getMessage("log.spec.message.size.min",new Object[]{checkElement.getMinSize()},locale));
                messageBuilder.append(messageSource.getMessage("log.spec.message.size.max",new Object[]{checkElement.getMaxSize()},locale));
                messageBuilder.append(messageSource.getMessage("log.spec.message.whitespace",new Object[]{checkElement.isWithWhiteSpace()},locale));
                messageBuilder.append(Constants.LINE);
                break;
        }

        return messageBuilder.toString();
    }
}
