package com.sunny.ai.common.vector;

import java.util.List;

public interface VectorSearchClient {

    void store(String documentId,
            String chunkId,
            String content,
            float[] vector);

    List<String> search(float[] queryVector, int topK);
}
