package org.evosuite.models;

import okhttp3.*;
import org.evosuite.testcase.statements.Statement;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Convert;

import java.io.IOException;
import java.util.*;

public class LanguageModelService {
    private OkHttpClient client;
    private String baseUrl;
    private String apiKey;
    private String token = "sk-UmNLNh140BD73A8B6702T3BLbkFJ60e3BFe87B6546718d18";

    public LanguageModelService(String baseUrl, String apiKey) {
        this.client = new OkHttpClient();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }
    public Statement convert(String testCode){

    }

    public List<Statement> generateTestsForUncoveredFunctions(Set<String> uncoveredFunctions) {
        List<Statement> generatedTests = new ArrayList<>();
        for (String function : uncoveredFunctions) {
            String testCode = generateTest(function);
            Statement testStatement= convert(testCode);
            generatedTests.add(testStatement);
        }
        return generatedTests;
    }

    private String generateTest(String functionName) {
        String url = baseUrl + "/generate-test";
        String jsonRequestBody = buildRequestBody(functionName);

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(jsonRequestBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try {
            Response response = client.newCall(request).execute();
            // Assuming the response body contains the generated test code directly
            return response.body().string();
        } catch (IOException e) {
            System.out.println("Failed to generate test for function: " + functionName);
            e.printStackTrace();
            return "";
        }
    }

    private String buildRequestBody(String functionName) {
        return String.format("{\"functionName\": \"%s\"}", functionName);
    }
}
