package artie.pedagogicalintervention.webservice.test;

import artie.pedagogicalintervention.webservice.service.ChatGPTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class ChatGPTServiceTest {

    @Autowired
    private ChatGPTService chatGPTService;

    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    void getChatResponseTest() throws Exception {
        String response = this.chatGPTService.getChatResponse("Hola");
        assertNotEquals("", response);
    }
}
