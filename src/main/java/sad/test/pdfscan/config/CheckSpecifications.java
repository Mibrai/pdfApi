package sad.test.pdfscan.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import sad.test.pdfscan.model.CheckElement;

import java.util.List;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "check-specification",ignoreInvalidFields = true, ignoreUnknownFields = true)
public class CheckSpecifications {
    private List<String> activeElementsToCheck;
    private List<CheckElement> elements;

    public List<String> getActiveElementsToCheck() {
        return activeElementsToCheck;
    }

    public void setActiveElementsToCheck(List<String> activeElementsToCheck) {
        this.activeElementsToCheck = activeElementsToCheck;
    }

    public List<CheckElement> getElements() {
        return elements;
    }

    public void setElements(List<CheckElement> elements) {
        this.elements = elements;
    }
}
