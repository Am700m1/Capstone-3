package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.Models.Contract;
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

    private static final String GEMINI_BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    @Value("${GEMINI_API}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String generateText(String prompt) {
        String url = GEMINI_BASE_URL + "?key=" + apiKey;

        try {
            String requestBody = buildRequestBody(prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            return extractGeneratedText(response.getBody());

        } catch (HttpClientErrorException e) {
            throw new ApiException("Gemini error " + e.getStatusCode() + ": " + e.getResponseBodyAsString());

        } catch (Exception e) {
            throw new ApiException("Gemini call failed: " + e.getMessage());
        }
    }

    public String generateOwnerReviewAnalysis(Owner owner, List<Review> reviews) {
        String prompt = buildOwnerAnalysisPrompt(owner, reviews);
        return generateText(prompt);
    }

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

    private String buildRequestBody(String prompt) throws Exception {
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode contents = objectMapper.createArrayNode();
        ObjectNode content = objectMapper.createObjectNode();
        ArrayNode parts = objectMapper.createArrayNode();
        ObjectNode part = objectMapper.createObjectNode();

        part.put("text", prompt);
        parts.add(part);
        content.set("parts", parts);
        contents.add(content);
        root.set("contents", contents);

        return objectMapper.writeValueAsString(root);
    }

    private String extractGeneratedText(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text")
                .asText("No explanation available.");
    }

    // CONTRACT ANALYSIS AI ENDPOINT!
    public String analyzeContract(Contract contract, String language) {
        String prompt = buildContractAnalysisPrompt(contract, language);
        String rawResponse = generateText(prompt);
        return cleanJsonResponse(rawResponse);
    }

    private String buildContractAnalysisPrompt(Contract contract, String language) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert legal AI assistant specializing in the Saudi real estate market.\n");
        prompt.append("Analyze the following contract details.\n\n");
        prompt.append("Start Date: ").append(contract.getStartDate()).append("\n");
        prompt.append("End Date: ").append(contract.getEndDate()).append("\n");
        prompt.append("Monthly Rent: ").append(contract.getMonthlyRent()).append(" SAR\n");
        prompt.append("Security Deposit: ").append(contract.getSecurityDeposit()).append(" SAR\n\n");

        prompt.append("Task:\n");
        prompt.append("1. Provide a brief summary of the financial and time commitments.\n");
        prompt.append("2. Highlight any irregularities, financial risks, or unusual terms (e.g., if the security deposit is unusually high compared to the rent, or if the duration is irregular).\n");
        prompt.append("3. Give a final recommendation (Proceed, Negotiate, or Walk Away).\n\n");

        prompt.append("CRITICAL INSTRUCTIONS:\n");
        prompt.append("- You must output ONLY a valid JSON object. Do not add any conversational text.\n");
        prompt.append("- The JSON keys must be exactly in English: \"summary\", \"irregularities\" (a list of strings), and \"recommendation\".\n");
        prompt.append("- The actual VALUES inside the JSON must be written entirely in ").append(language).append(".\n");

        return prompt.toString();
    }

    // Helper method to remove markdown formatting if Gemini adds it
    private String cleanJsonResponse(String response) {
        response = response.trim();
        if (response.startsWith("```json")) {
            response = response.substring(7);
        } else if (response.startsWith("```")) {
            response = response.substring(3);
        }
        if (response.endsWith("```")) {
            response = response.substring(0, response.length() - 3);
        }
        return response.trim();
    }
}
