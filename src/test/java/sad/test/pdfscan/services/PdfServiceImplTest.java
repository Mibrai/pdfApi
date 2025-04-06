package sad.test.pdfscan.services;

import com.google.common.truth.Truth;
import org.apache.commons.io.FileDeleteStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.meta.When;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = true)
public class PdfServiceImplTest {

    @Mock
    private PdfServiceImpl pdfService;

    @Autowired
    private MockMvc mockMvc;
    @Test
    void downloadAndStorePdfTest() throws IOException {

    }
}
