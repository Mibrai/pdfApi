package sad.test.pdfscan.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

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
