package artie.pedagogicalintervention.webservice.service;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatGPTService {

    @Value("${artie.chatgpt.api.key}")
    private String apiKey;

    @Value("${artie.chatgpt.api.url}")
    private String APIUrl;

    @Value("${artie.chatgpt.api.maxtokens}")
    private Integer maxTokens;

    /**
     * Function to answer ChatGPT and get the text
     * @param answer
     * @return response from ChatGPT
     * @throws Exception
     */
    public String getChatResponse(String answer) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(APIUrl);

        // Building the request
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("prompt", answer);
        jsonObject.put("max_tokens", maxTokens);
        jsonObject.put("temperature", 0.7);

        StringEntity entity = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        // Adding headers
        httpPost.setHeader("Authorization", "Bearer " + this.apiKey);
        httpPost.setHeader("Content-Type", "application/json");

        // Processing the answer
        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity, "UTF-8");
            JSONObject jsonResponse = new JSONObject(responseString);
            String chatResponse = jsonResponse.getJSONArray("choices").getJSONObject(0).getString("text");
            EntityUtils.consume(responseEntity);
            System.out.println(chatResponse);
            return chatResponse;
        }
    }
}
