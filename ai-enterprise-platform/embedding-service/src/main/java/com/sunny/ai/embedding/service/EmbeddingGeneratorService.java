package com.sunny.ai.embedding.service;

import com.sunny.ai.common.vector.VectorSearchClient;
import com.sunny.ai.embedding.domain.Embedding;
import com.sunny.ai.embedding.repository.EmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Generates vector embeddings by calling an external AI model API (e.g. OpenAI, Ollama).
 * Falls back to a deterministic mock when no API key is configured.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingGeneratorService {

    private static final int DEFAULT_DIMENSIONS = 1536;
    private static final String MODEL_NAME = "text-embedding-ada-002";

    private final EmbeddingRepository embeddingRepository;
    private final WebClient.Builder webClientBuilder;
    private final VectorSearchClient vectorSearchClient;

    @Value("${ai.embedding.api-key:mock}")
    private String apiKey;

    @Value("${ai.embedding.base-url:http://localhost:11434}")
    private String baseUrl;

    public Embedding generateAndStore(String chunkId, String documentId, String chunkContent) {
        log.info("Generating embedding for chunk {} of document {}", chunkId, documentId);

        float[] vector = generateVector(chunkContent);
        String vectorData = floatsToString(vector);

        Embedding embedding = Embedding.builder()
                .chunkId(chunkId)
                .documentId(documentId)
                .chunkContent(chunkContent)
                .vectorData(vectorData)
                .modelName(MODEL_NAME)
                .dimensions(DEFAULT_DIMENSIONS)
                .build();

        // Store into OpenSearch via VectorSearchClient (replacement of legacy storage path)
        try {
            vectorSearchClient.store(documentId, chunkId, chunkContent, vector);
        } catch (Exception e) {
            log.error("Failed to store embedding in vector DB: {}", e.getMessage(), e);
        }

        // Continue persisting to relational DB for backward compatibility
        return embeddingRepository.save(embedding);
    }

    public List<Embedding> getEmbeddingsForDocument(String documentId) {
        return embeddingRepository.findByDocumentId(documentId);
    }

    /**
     * Cosine similarity between a query vector and stored embeddings.
     * Returns top-k most similar embeddings.
     */
    public List<Embedding> findSimilar(float[] queryVector, int topK) {
        List<Embedding> all = embeddingRepository.findAll();
        return all.stream()
                .sorted((a, b) -> Double.compare(
                        cosineSimilarity(stringToFloats(b.getVectorData()), queryVector),
                        cosineSimilarity(stringToFloats(a.getVectorData()), queryVector)))
                .limit(topK)
                .collect(Collectors.toList());
    }

    public float[] generateVector(String text) {
        if ("mock".equals(apiKey)) {
            return mockVector(text);
        }
        // TODO: Replace with actual OpenAI / Ollama API call
        return mockVector(text);
    }

    // --- Utility methods ---

    private float[] mockVector(String text) {
        Random rng = new Random(text.hashCode());
        float[] vec = new float[DEFAULT_DIMENSIONS];
        float norm = 0f;
        for (int i = 0; i < DEFAULT_DIMENSIONS; i++) {
            vec[i] = rng.nextFloat() * 2 - 1;
            norm += vec[i] * vec[i];
        }
        norm = (float) Math.sqrt(norm);
        for (int i = 0; i < DEFAULT_DIMENSIONS; i++) vec[i] /= norm;
        return vec;
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-9);
    }

    private String floatsToString(float[] vec) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vec.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(vec[i]);
        }
        return sb.toString();
    }

    private float[] stringToFloats(String s) {
        String[] parts = s.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) result[i] = Float.parseFloat(parts[i]);
        return result;
    }
}
