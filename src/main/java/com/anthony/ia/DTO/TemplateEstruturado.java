package com.anthony.ia.DTO;

import dev.langchain4j.model.input.structured.StructuredPrompt;

import java.util.List;

public class TemplateEstruturado {

    @StructuredPrompt({"Crie uma receita de {{prato}} usando os seguintes ingredientes: {{ingredientes}}.",
            "Estruture a sua resposta dessa seguinte forma: ",
    "Nome do prato: {{prato}}",
    "Ingredientes: {{ingredientes}}",
    "Modo de preparo: explique o passo a passo de como preparar o prato usando os ingredientes listados.",
    "Tempo de preparo: de o tempo de preparo para formas diferente forno fogao e airfryer por exemplo",})

    public static class PromptDeReceita {
        public String prato;
        public List<String> ingredientes;
    }
}
