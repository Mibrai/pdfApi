package sad.test.pdfscan.utils;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import sad.test.pdfscan.config.BlackListedProperties;
import sad.test.pdfscan.config.CountriesSpecificationProperties;
import sad.test.pdfscan.config.DefaultSpecificationProperties;
import sad.test.pdfscan.model.CheckElement;
import sad.test.pdfscan.model.Country;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        if(checkElement.getName().contains(Constants.IBAN)){
            if(defaultSpecificationProperties != null &&
                    element != null &&
                    !element.isBlank() &&
                    defaultSpecificationProperties.getCountry().equalsIgnoreCase(element.substring(0,2))){
                if(defaultSpecificationProperties.isWithWhiteSpace()){
                    return element.replaceAll(" ","").length() == defaultSpecificationProperties.getSize();
                } else {
                    return element.length() == (defaultSpecificationProperties.getSize() + (int)(defaultSpecificationProperties.getSize() / 4));
                }
            }
        }

        if(!checkElement.getName().contains(Constants.IBAN)){
            return element.length() == checkElement.getSize();
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
    public static boolean isBlackListed(final String element, final BlackListedProperties blackListedProperties){
        boolean state = false;

        if(element == null || element.isBlank())
            return false;

        String blockIban = element.trim().replaceAll(" ","");

        if(blackListedProperties.getIbans().contains(blockIban) ||
                blackListedProperties.getIbans().contains(element.trim()))
            state = true;

        if(blackListedProperties.getCountries().contains(element.trim().substring(0,2)))
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
     *
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
}
