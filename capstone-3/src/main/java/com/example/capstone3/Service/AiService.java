package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.Models.Owner;
import com.example.capstone3.Models.Review;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    // This constant stores the Gemini endpoint used to generate text.
    private static final String GEMINI_BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    // Spring reads the Gemini API key from application configuration.
    @Value("${GEMINI_API}")
    private String apiKey;

    // RestTemplate sends prompts to Gemini.
    private final RestTemplate restTemplate;
    // ObjectMapper builds Gemini request JSON and reads response JSON.
    private final ObjectMapper objectMapper;

    // Gemini only writes text from supplied facts; it does not read or update project data.
    public String generateText(String prompt, String language) {
        if (!"AR".equals(language) && !"EN".equals(language)) {
            throw new ApiException("Language must be AR or EN");
        }
        // The API key is sent as a query parameter required by Gemini.
        String url = GEMINI_BASE_URL + "?key=" + apiKey;

        try {
            // Language changes Gemini's response only; stored project data is unchanged.
            String languageInstruction = "AR".equals(language)
                    ? "Respond in Arabic.\n\n"
                    : "Respond in English.\n\n";
            // Build the JSON body in the format required by generateContent.
            String requestBody = buildRequestBody(languageInstruction + prompt);

            // HttpHeaders stores metadata about the request.
            HttpHeaders headers = new HttpHeaders();
            // Gemini expects the request body to contain JSON.
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HttpEntity combines the JSON request body and its headers.
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // exchange sends a POST request and returns the full HTTP response.
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            // Return only Gemini's generated text instead of its full JSON response.
            return extractGeneratedText(response.getBody());

        } catch (HttpClientErrorException e) {
            // Keep Gemini's HTTP status and response details for a clear API error.
            throw new ApiException("Gemini error " + e.getStatusCode() + ": " + e.getResponseBodyAsString());

        } catch (Exception e) {
            // Convert other HTTP or JSON failures into the project's API exception.
            throw new ApiException("Gemini call failed: " + e.getMessage());
        }
    }

    // Sends owner details and tenant reviews to Gemini for a written analysis.
    public String generateOwnerReviewAnalysis(Owner owner, List<Review> reviews, String language) {
        String prompt = buildOwnerAnalysisPrompt(owner, reviews);
        return generateText(prompt, language);
    }

    // Builds the owner-review facts and instructions Gemini is allowed to analyze.
    private String buildOwnerAnalysisPrompt(Owner owner, List<Review> reviews) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the following tenant reviews for a property owner and provide a professional summary.\n\n");
        prompt.append("Owner: ").append(owner.getFullName()).append("\n");
        prompt.append("Total Reviews: ").append(reviews.size()).append("\n\n");
        prompt.append("=== REVIEWS ===\n");
        for (int i = 0; i < reviews.size(); i++) {
            Review r = reviews.get(i);
            prompt.append("\nReview ").append(i + 1).append(":\n");
            prompt.append("Rating: ").append(r.getRating()).append("/5\n");
            prompt.append("Comment: ").append(r.getComment()).append("\n");
        }
        prompt.append("\n=== YOUR TASK ===\n");
        prompt.append("Based on the reviews, provide a professional analysis covering:\n");
        prompt.append("1. Overall tenant satisfaction\n");
        prompt.append("2. Common complaints\n");
        prompt.append("3. Common strengths\n");
        prompt.append("4. Communication quality\n");
        prompt.append("5. Maintenance quality\n");
        prompt.append("6. A recommendation score out of 10\n");
        prompt.append("7. A final summary paragraph\n");
        prompt.append("Write in a clear and professional tone.");
        return prompt.toString();
    }

    // Creates the JSON structure required by the Gemini generateContent API.
    private String buildRequestBody(String prompt) throws Exception {
        // ObjectNode represents a JSON object and ArrayNode represents a JSON array.
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode contents = objectMapper.createArrayNode();
        ObjectNode content = objectMapper.createObjectNode();
        ArrayNode parts = objectMapper.createArrayNode();
        ObjectNode part = objectMapper.createObjectNode();

        // Place the prompt inside Gemini's contents, parts, and text structure.
        part.put("text", prompt);
        parts.add(part);
        content.set("parts", parts);
        contents.add(content);
        root.set("contents", contents);

        return objectMapper.writeValueAsString(root);
    }

    // Reads the first generated text result from the Gemini response.
    private String extractGeneratedText(String responseBody) throws Exception {
        // readTree converts Gemini's JSON text into nodes that can be navigated.
        JsonNode root = objectMapper.readTree(responseBody);
        // path follows the response fields to the first generated candidate.
        return root.path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text")
                .asText("No explanation available.");
    }
}
