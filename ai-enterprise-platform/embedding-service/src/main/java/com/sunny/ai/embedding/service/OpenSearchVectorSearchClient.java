package com.sunny.ai.embedding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import com.sunny.ai.common.vector.VectorSearchClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.RequestOptions;


@Service
@RequiredArgsConstructor
@Slf4j
public class OpenSearchVectorSearchClient implements VectorSearchClient {

  private static final String INDEX = "embeddings-index";

  private final RestHighLevelClient client;

  @Override
  public void store(String documentId,
      String chunkId,
      String content,
      float[] vector) {

    Map<String, Object> doc = new HashMap<>();
    doc.put("documentId", documentId);
    doc.put("chunkId", chunkId);
    doc.put("chunkContent", content);
    doc.put("embedding", vector);

    IndexRequest request = new IndexRequest(INDEX)
        .id(chunkId)
        .source(doc);

    try {
      client.index(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new RuntimeException("Failed to store embedding", e);
    }
  }

  @Override
  public List<String> search(float[] queryVector, int topK) {

    String knnQuery = """
        {
          "size": %d,
          "query": {
            "knn": {
              "embedding": {
                "vector": %s,
                "k": %d
              }
            }
          }
        }
        """.formatted(topK,
        Arrays.toString(queryVector),
        topK);

    SearchRequest request = new SearchRequest(INDEX);
    request.source(new SearchSourceBuilder()
        .query(QueryBuilders.wrapperQuery(knnQuery)));

    try {
      SearchResponse response = client.search(request, RequestOptions.DEFAULT);

      return Arrays.stream(response.getHits().getHits())
          .map(hit -> (String) hit.getSourceAsMap().get("chunkContent"))
          .toList();

    } catch (IOException e) {
      throw new RuntimeException("Vector search failed", e);
    }
  }
}
