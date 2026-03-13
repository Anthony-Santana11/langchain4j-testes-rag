# LangChain4j + RAG — Guia Completo

> Aprenda a usar **LangChain4j** e **RAG (Retrieval-Augmented Generation)** para construir aplicações de IA em Java, tanto em projetos pequenos quanto em larga escala.

[![Maven Central](https://img.shields.io/maven-central/v/dev.langchain4j/langchain4j.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/dev.langchain4j/langchain4j)

> **📌 Versão usada nos exemplos:** `0.36.2`
> Para usar a versão mais recente, substitua `0.36.2` pela versão atual disponível no [Maven Central](https://central.sonatype.com/artifact/dev.langchain4j/langchain4j).

---

## Índice

1. [O que é LangChain4j?](#o-que-é-langchain4j)
2. [O que é RAG?](#o-que-é-rag)
3. [Como o RAG funciona](#como-o-rag-funciona)
4. [Configuração e Dependências](#configuração-e-dependências)
5. [RAG em Projetos Pequenos](#rag-em-projetos-pequenos)
6. [RAG em Larga Escala](#rag-em-larga-escala)
7. [Exemplos de Código](#exemplos-de-código)
8. [Boas Práticas](#boas-práticas)
9. [Recursos Adicionais](#recursos-adicionais)

---

## O que é LangChain4j?

**LangChain4j** é um framework Java inspirado no LangChain (Python) que facilita a integração de **Large Language Models (LLMs)** em aplicações Java e Kotlin. Ele fornece abstrações de alto nível para:

- Conectar-se a provedores de LLM (OpenAI, Anthropic, Ollama, Azure, Hugging Face, etc.)
- Gerenciar memória de conversação (histórico de chat)
- Criar *chains* (cadeias) de processamento de texto
- Implementar agentes autônomos com uso de ferramentas
- Construir pipelines de **RAG** (Retrieval-Augmented Generation)

### Por que usar LangChain4j?

| Característica | Benefício |
|---|---|
| API fluente em Java | Integração natural com ecossistema Java/Spring |
| Suporte a múltiplos LLMs | Troca de provedor sem alterar a lógica de negócio |
| RAG nativo | Componentes prontos para embeddings e busca semântica |
| Integração com Spring Boot | Starter oficial para configuração mínima |
| Agentes e ferramentas | Permite LLMs realizarem ações no sistema |

---

## O que é RAG?

**RAG (Retrieval-Augmented Generation)** é uma técnica que combina a capacidade generativa dos LLMs com a busca em bases de conhecimento externas. Em vez de depender apenas do conhecimento "embutido" no modelo durante o treinamento, o RAG:

1. **Recupera** documentos relevantes de uma base de dados (ex.: PDFs, wikis, banco de dados)
2. **Aumenta** o prompt do LLM com esse contexto adicional
3. **Gera** uma resposta fundamentada nos documentos recuperados

### Vantagens do RAG

- ✅ Respostas baseadas em dados atualizados (sem re-treinar o modelo)
- ✅ Redução de *hallucinations* (alucinações do modelo)
- ✅ Citação de fontes e rastreabilidade
- ✅ Controle sobre o domínio do conhecimento
- ✅ Menor custo comparado ao *fine-tuning*

---

## Como o RAG funciona

```
┌─────────────────────────────────────────────────────────────┐
│                      FASE DE INDEXAÇÃO                      │
│                                                             │
│  Documentos → Chunking → Embeddings → Vector Store (índice) │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    FASE DE CONSULTA (RAG)                   │
│                                                             │
│  Pergunta do usuário                                        │
│       ↓                                                     │
│  Embedding da pergunta                                      │
│       ↓                                                     │
│  Busca por similaridade no Vector Store                     │
│       ↓                                                     │
│  Chunks relevantes recuperados                              │
│       ↓                                                     │
│  Prompt aumentado (pergunta + contexto)                     │
│       ↓                                                     │
│  LLM gera resposta baseada no contexto                      │
│       ↓                                                     │
│  Resposta final ao usuário                                  │
└─────────────────────────────────────────────────────────────┘
```

### Componentes principais

| Componente | Descrição | Exemplos no LangChain4j |
|---|---|---|
| **Document Loader** | Carrega documentos de diversas fontes | `FileSystemDocumentLoader`, `UrlDocumentLoader` |
| **Document Splitter** | Divide documentos em *chunks* | `DocumentSplitters.recursive()` |
| **Embedding Model** | Converte texto em vetores numéricos | `OpenAiEmbeddingModel`, `OllamaEmbeddingModel` |
| **Vector Store** | Armazena e busca embeddings | `InMemoryEmbeddingStore`, `PgVectorEmbeddingStore`, `ChromaEmbeddingStore` |
| **Content Retriever** | Recupera chunks relevantes | `EmbeddingStoreContentRetriever` |
| **Chat Model** | Gera a resposta final | `OpenAiChatModel`, `AnthropicChatModel` |

---

## Configuração e Dependências

### Maven

```xml
<dependencies>
    <!-- Core do LangChain4j -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j</artifactId>
        <version>0.36.2</version>
    </dependency>

    <!-- Integração com OpenAI -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-open-ai</artifactId>
        <version>0.36.2</version>
    </dependency>

    <!-- Vector Store em memória (ideal para projetos pequenos) -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
        <version>0.36.2</version>
    </dependency>

    <!-- Spring Boot Starter (opcional) -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-spring-boot-starter</artifactId>
        <version>0.36.2</version>
    </dependency>
</dependencies>
```

### Gradle

```groovy
dependencies {
    implementation 'dev.langchain4j:langchain4j:0.36.2'
    implementation 'dev.langchain4j:langchain4j-open-ai:0.36.2'
    implementation 'dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:0.36.2'

    // Spring Boot (opcional)
    implementation 'dev.langchain4j:langchain4j-spring-boot-starter:0.36.2'
}
```

### Variáveis de ambiente

```bash
# OpenAI
export OPENAI_API_KEY=sk-...

# Ollama (local, sem custo)
# Nenhuma chave necessária — basta ter o Ollama rodando localmente
```

---

## RAG em Projetos Pequenos

Para projetos pequenos (protótipos, MVPs, ferramentas internas), o LangChain4j oferece uma configuração mínima usando:

- **Vector Store em memória** (`InMemoryEmbeddingStore`) — sem necessidade de banco de dados externo
- **Modelo de embedding local** (`AllMiniLmL6V2EmbeddingModel`) — sem custo de API
- **Interface AI Service** — abstração simples via interface Java

### Exemplo: Chatbot com RAG sobre documentos locais

```java
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;

public class RagSimples {

    // 1. Defina a interface do assistente
    interface Assistente {
        String responder(String pergunta);
    }

    public static void main(String[] args) {

        // 2. Carregue e indexe os documentos
        List<Document> documentos = FileSystemDocumentLoader.loadDocuments("./documentos");

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 30))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(documentos);

        // 3. Configure o modelo de linguagem
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini")
                .build();

        // 4. Configure o retriever
        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.7)
                .build();

        // 5. Monte o AI Service com RAG
        Assistente assistente = AiServices.builder(Assistente.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(retriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        // 6. Use!
        String resposta = assistente.responder("O que diz o documento sobre prazos de entrega?");
        System.out.println(resposta);
    }
}
```

### Configuração com Spring Boot (projeto pequeno)

```java
// application.properties
// langchain4j.open-ai.chat-model.api-key=${OPENAI_API_KEY}
// langchain4j.open-ai.chat-model.model-name=gpt-4o-mini

@Service
public class ChatbotService {

    private final Assistente assistente;

    public ChatbotService(
            ChatLanguageModel chatModel,
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel) {

        ContentRetriever retriever = EmbeddingStoreContentRetriever.from(embeddingStore);

        this.assistente = AiServices.builder(Assistente.class)
                .chatLanguageModel(chatModel)
                .contentRetriever(retriever)
                .build();
    }

    public String perguntar(String pergunta) {
        return assistente.responder(pergunta);
    }
}
```

---

## RAG em Larga Escala

Para aplicações em produção com alto volume de documentos e requisições, a arquitetura precisa evoluir em vários aspectos:

### Arquitetura recomendada

```
┌───────────────────────────────────────────────────────────────┐
│                     PIPELINE DE INDEXAÇÃO                     │
│                                                               │
│  Fontes de dados (S3, DB, APIs, SharePoint)                   │
│       ↓                                                       │
│  Document Loaders (assíncronos, batch)                        │
│       ↓                                                       │
│  Pré-processamento (limpeza, metadados, OCR)                  │
│       ↓                                                       │
│  Chunking estratégico (por seção, por parágrafo)              │
│       ↓                                                       │
│  Embedding em lote (batch) via API ou modelo dedicado         │
│       ↓                                                       │
│  Vector Store distribuído (PgVector, Pinecone, Weaviate)      │
└───────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────┐
│                    PIPELINE DE CONSULTA                       │
│                                                               │
│  API Gateway / Load Balancer                                  │
│       ↓                                                       │
│  Cache de consultas (Redis)                                   │
│       ↓                                                       │
│  Query Rewriting / Expansion (melhora a recuperação)          │
│       ↓                                                       │
│  Hybrid Search (semântico + BM25 full-text)                   │
│       ↓                                                       │
│  Re-ranking dos resultados (Cross-Encoder)                    │
│       ↓                                                       │
│  LLM com contexto otimizado                                   │
│       ↓                                                       │
│  Resposta + metadados de rastreabilidade                      │
└───────────────────────────────────────────────────────────────┘
```

### Vector Stores para produção

| Vector Store | Melhor para | Integração LangChain4j |
|---|---|---|
| **PgVector** | Já usa PostgreSQL; até ~1M vetores | `langchain4j-pgvector` |
| **Pinecone** | SaaS gerenciado; escala automática | `langchain4j-pinecone` |
| **Weaviate** | Open-source; busca híbrida nativa | `langchain4j-weaviate` |
| **Chroma** | Protótipos e ambientes locais | `langchain4j-chroma` |
| **Redis** | Baixa latência; já usa Redis | `langchain4j-redis` |
| **OpenSearch** | Full-text + vetores; AWS managed | `langchain4j-opensearch` |

### Exemplo: RAG com PgVector (produção)

```xml
<!-- Adicione ao pom.xml -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-pgvector</artifactId>
    <version>0.36.2</version>
</dependency>
```

```java
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
        .host("localhost")
        .port(5432)
        .database("meu_banco")
        .user("postgres")
        .password(System.getenv("DB_PASSWORD"))
        .table("embeddings_documentos")
        .dimension(384) // dimensão do modelo de embedding escolhido
        .createTable(true)
        .build();
```

### Exemplo: Ingestão em lote (batch ingestion)

```java
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;

import java.util.List;

public class BatchIngestor {

    private final EmbeddingStoreIngestor ingestor;

    public BatchIngestor(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> store) {
        this.ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(512, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(store)
                .build();
    }

    public void ingerirEmLotes(List<Document> documentos, int tamanhoDeLote) {
        for (int i = 0; i < documentos.size(); i += tamanhoDeLote) {
            List<Document> lote = documentos.subList(i,
                    Math.min(i + tamanhoDeLote, documentos.size()));
            ingestor.ingest(lote);
            System.out.printf("Processados %d/%d documentos%n",
                    Math.min(i + tamanhoDeLote, documentos.size()), documentos.size());
        }
    }
}
```

### Exemplo: RAG avançado com filtro de metadados

```java
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;

ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
        .embeddingStore(embeddingStore)
        .embeddingModel(embeddingModel)
        .maxResults(5)
        .minScore(0.75)
        // Filtra apenas documentos de um departamento específico
        .filter(MetadataFilterBuilder.metadataKey("departamento").isEqualTo("juridico"))
        .build();
```

### Configuração Spring Boot para produção

```yaml
# application.yml
langchain4j:
  open-ai:
    chat-model:
      api-key: ${OPENAI_API_KEY}
      model-name: gpt-4o
      temperature: 0.2
      max-tokens: 2048

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/meu_banco
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

---

## Exemplos de Código

### 1. Usando modelo local com Ollama (sem custo de API)

```java
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;

// Modelo de chat local
ChatLanguageModel chatModel = OllamaChatModel.builder()
        .baseUrl("http://localhost:11434")
        .modelName("llama3.2")
        .build();

// Modelo de embedding local
EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
        .baseUrl("http://localhost:11434")
        .modelName("nomic-embed-text")
        .build();
```

### 2. AI Service com anotações avançadas

```java
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

interface AssistenteJuridico {

    @SystemMessage("""
            Você é um assistente jurídico especializado.
            Responda apenas com base nos documentos fornecidos.
            Se não souber a resposta, diga que não encontrou informações suficientes.
            Sempre cite a fonte dos documentos utilizados.
            """)
    @UserMessage("Pergunta sobre o contrato {{numero}}: {{pergunta}}")
    String consultarContrato(
            @V("numero") String numeroContrato,
            @V("pergunta") String pergunta);
}
```

### 3. RAG com múltiplas fontes de recuperação

```java
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.content.retriever.WebSearchContentRetriever;

// Recuperador da base de documentos interna
ContentRetriever retrieverInterno = EmbeddingStoreContentRetriever.from(embeddingStore);

// Monte o assistente com múltiplos retrievers
Assistente assistente = AiServices.builder(Assistente.class)
        .chatLanguageModel(chatModel)
        .contentRetriever(retrieverInterno)
        .build();
```

---

## Boas Práticas

### Chunking (Divisão de documentos)

| Estratégia | Quando usar |
|---|---|
| **Tamanho fixo** (ex.: 512 tokens) | Documentos uniformes, início rápido |
| **Recursivo** (por parágrafo/seção) | Documentos estruturados (artigos, manuais) |
| **Por sentença** | Documentos de perguntas e respostas |
| **Hierárquico** | Documentos longos com estrutura de seções |

- Use **sobreposição** (*overlap*) de 10-15% entre chunks para não perder contexto na divisão
- Inclua **metadados** (fonte, data, autor, seção) em cada chunk para filtragem e rastreabilidade

### Embeddings

- Escolha o modelo de embedding adequado ao idioma dos documentos (ex.: `multilingual-e5-large` para português)
- Mantenha **consistência**: use o mesmo modelo na indexação e na consulta
- **Nunca misture** embeddings de modelos diferentes no mesmo vector store

### Recuperação

- Ajuste `maxResults` e `minScore` com base em testes empíricos
- Considere **busca híbrida** (semântica + full-text) para maior precisão
- Implemente **re-ranking** com Cross-Encoder para reordenar os resultados antes de enviar ao LLM

### Prompts

- Instrua o modelo a **citar as fontes** usadas na resposta
- Defina um comportamento claro para quando **não há contexto suficiente**
- Use temperatura baixa (`temperature: 0.1–0.3`) para respostas mais factuais

### Performance em produção

- Cache de embeddings de consultas frequentes com **Redis**
- Processe a ingestão de documentos de forma **assíncrona** (ex.: fila de mensagens)
- Monitore latência, tokens consumidos e qualidade das respostas com **observabilidade**

---

## Recursos Adicionais

- 📚 [Documentação oficial do LangChain4j](https://docs.langchain4j.dev)
- 💻 [Repositório oficial no GitHub](https://github.com/langchain4j/langchain4j)
- 🗂️ [Exemplos oficiais](https://github.com/langchain4j/langchain4j-examples)
- 🤗 [Modelos de embedding no Hugging Face](https://huggingface.co/models?pipeline_tag=feature-extraction)
- 🦙 [Ollama — LLMs locais](https://ollama.com)
- 🐘 [PgVector — extensão PostgreSQL para vetores](https://github.com/pgvector/pgvector)

---

## Licença

Este projeto é distribuído sob a licença MIT. Consulte o arquivo `LICENSE` para mais detalhes.