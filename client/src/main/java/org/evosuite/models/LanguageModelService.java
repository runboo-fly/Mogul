package org.evosuite.models;

import okhttp3.*;
import org.evosuite.testcase.TestCodeParser;
import org.evosuite.testcase.statements.Statement;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Convert;

import java.io.IOException;
import java.util.*;

public class LanguageModelService {
    private OkHttpClient client ;

    private String token = "sk-UmNLNh140BD73A8B6702T3BLbkFJ60e3BFe87B6546718d18";

    public LanguageModelService() {
        this.client = new OkHttpClient();

    }
    public Statement convert(String testCode) {
        return TestCodeParser.parse(testCode);
    }
    private static class Payload {
        String model;
        List<requestMessage> requetmessages;

        Payload(String model, List<requestMessage> requetmessages) {
            this.model = model;
            this.requetmessages = requetmessages;
        }
    }
    public List<Statement> generateTestsForUncoveredFunctions(Set<String> uncoveredFunctions) {
        List<Statement> generatedTests = new ArrayList<>();
        for (String function : uncoveredFunctions) {
            String testCode = generateTest(function);
            if (testCode != null && !testCode.isEmpty()) {
                Statement testStatement = convert(testCode);
                if (testStatement != null) {
                    generatedTests.add(testStatement);
                }
            }
        }
        return generatedTests;
    }
    private static class requestMessage {
        String role;
        String content;

        requestMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    private String generateTest(String functionName) {
        String url = "https://cn2us02.opapi.win/v1/chat/completions";
        RequestBody body = buildRequestBody(functionName);

        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Content-Type", "application/json")
//                .post(RequestBody.create(MediaType.parse("application/json"), jsonRequestBody))
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                System.out.println("Request failed: " + response);
                return "";
            }
        } catch (IOException e) {
            System.out.println("Failed to generate test for function: " + functionName);
            e.printStackTrace();
            return "";
        }
    }

    private RequestBody buildRequestBody(String functionName) {
//        return String.format("{\"functionName\": \"%s\"}", functionName);
    }
    private static RequestBody buildRequestBody() {

        String model = "gpt-3.5-turbo";
        String systemContent = "You are a useful AI assistant, using your coding and language skills to generate unit test for Java programs. Use junit5 or mockito 4.x when you need.";
        String userContent = "this is a java class:" +
                "Write the code in a single test method as much as possible.";

        // 构建消息列表
        List<requestMessage> requetmessages = Arrays.asList(
                new requestMessage("system", systemContent),
                new requestMessage("user", userContent)
        );

        // 构建 payload 对象
        Payload payload = new Payload(model, requetmessages);

        // 转换 payload 为 JSON 字符串
        String payloadJson = "{\"model\":\"" + payload.model + "\",\"messages\":[";
        for (requestMessage requetmessage : payload.requetmessages) {
            payloadJson += "{\"role\":\"" + requetmessage.role + "\",\"content\":\"" + requetmessage.content + "\"},";
        }
        payloadJson = payloadJson.substring(0, payloadJson.length() - 1) + "]}";

        // 构建 RequestBody
        MediaType mediaType = MediaType.parse("application/json");
        return RequestBody.create(mediaType, payloadJson);
    }


}
