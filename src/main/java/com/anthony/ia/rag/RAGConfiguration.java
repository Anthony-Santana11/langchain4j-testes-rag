package com.anthony.ia.rag;

import ai.djl.util.Utils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;

@Service
public class RAGConfiguration {
    @Value("${langchain4j.google.ai.gemini.chat.api-key}")
    public String apiKey;

    public Assitant configure() throws Exception {
        List<Document> documents;
        // carregamento da lista de documentos a partir do sistema de arquivos usando o FileSystemDocumentLoader
        documents = FileSystemDocumentLoader.loadDocuments(toPath("document/"),glob("*.txt"));
        Assitant assistente = AiServices.builder(Assitant.class)
                .chatModel(GoogleAiGeminiChatModel.builder()
                        .apiKey(apiKey)
                        .modelName("gemini-2.5-flash")
                        .temperature(0.8)
                        .build())
                .contentRetriever(createContentRetriever(documents))
                .build();
        return assistente;

    }

    public ContentRetriever createContentRetriever(List<Document> documents) {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
        return EmbeddingStoreContentRetriever.from(embeddingStore);
    }

    public PathMatcher glob(String glob) {
        return FileSystems.getDefault().getPathMatcher("glob:" + glob);
    }

    public Path toPath (String path) {
        try {
            URL fileUrl = Utils.class.getClassLoader().getResource(path);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFileContent(){
        Resource resource = new ClassPathResource("document/text-anthony.txt");
        try{
            File file = resource.getFile();
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
