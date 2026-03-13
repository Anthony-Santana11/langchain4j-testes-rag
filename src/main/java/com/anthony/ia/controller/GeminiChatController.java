package com.anthony.ia.controller;

import com.anthony.ia.DTO.ChatQuestion;
import com.anthony.ia.DTO.TemplateEstruturado;
import com.anthony.ia.rag.Assitant;
import com.anthony.ia.rag.RAGConfiguration;
import dev.langchain4j.model.googleai.GoogleAiGeminiBatchImageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiImageModel;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.PostConstruct;

import java.net.MalformedURLException;
import java.sql.Array;
import java.util.Arrays;


@RestController
@RequiredArgsConstructor
public class GeminiChatController {

    @Value("${langchain4j.google.ai.gemini.chat.api-key}")
    private String apiKey;

    private ChatModel chatModel;

    @Autowired
    private RAGConfiguration ragConfiguration;


    private Assitant assistente;


    @PostConstruct
    public void init() {
        this.chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.8)
                .build();
    }


    @PostMapping("/chat")
    public String chatGemini(@RequestBody ChatQuestion question) {
        return chatModel.chat(question.question());
    }

    @GetMapping("/receita")
    public String receita() {
        TemplateEstruturado templateEstruturado = new TemplateEstruturado();
        TemplateEstruturado.PromptDeReceita prompt = new TemplateEstruturado.PromptDeReceita();
        prompt.prato = "Bolo de Chocolate";
        prompt.ingredientes = Arrays.asList("farinha", "açúcar", "cacau em pó", "ovos", "leite", "óleo");
        Prompt rcprompt = StructuredPromptProcessor.toPrompt(prompt);

        return chatModel.chat(prompt.prato + prompt.ingredientes);

    }

    @PostMapping("/image")
    public String createImg(@RequestBody ChatQuestion question) throws MalformedURLException {
        ImageModel imageModel = GoogleAiGeminiImageModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3-pro-image-preview")
                .build();

        return imageModel.generate(question.question()).content().url().toURL().toString();

    }

    @PostMapping("/chatwithrag")
    public String chatWithRag(@RequestBody ChatQuestion question) {
       if (assistente == null) {
           try {
               assistente = ragConfiguration.configure();
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
       }
        return assistente.answer(question.question());
    }
}
