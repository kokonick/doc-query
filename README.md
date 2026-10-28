# doc-query

A Spring Boot application that answers questions from the contents of your documents using AI models, running locally on the JVM with [Deep Java Library (DJL)](https://djl.ai/).

Upload or point it at a Word document, ask a factual question, and get back the answer found in the text.

<!-- TODO: add a screenshot or GIF of the web UI here -->

## Features

- Extracts text from Microsoft Word (`.docx`) files using Apache POI
- Answers factual questions with a Hugging Face question-answering model, run locally through DJL and PyTorch
- Server-rendered web UI built with Spring MVC and Thymeleaf
- Embedded SQLite database for persistence, accessed with Spring JDBC
- No external AI API and no API keys: the model runs on your machine

<!-- TODO: confirm and adjust the list above against the actual code, and add anything else the app does (history, multiple documents, REST endpoints, ...) -->

## Tech stack

| Area | Technology |
|------|------------|
| Language / runtime | Java 25 |
| Framework | Spring Boot 4.1.1 (Web MVC, Thymeleaf, JDBC, Validation) |
| AI inference | DJL 0.39.0, PyTorch engine, Hugging Face tokenizers |
| Document parsing | Apache POI (`poi-ooxml`) 5.5.1 |
| Database | SQLite (`sqlite-jdbc`) |
| Build | Maven (wrapper included) |

## How it works

1. **Extract:** the text of the Word document is read, including tables.
2. **Chunk:** long text is split into overlapping chunks, because QA models read a limited number of tokens at a time (about 512 for BERT/RoBERTa-style models).
3. **Rank:** chunks most relevant to the question are selected.
4. **Answer:** the QA model returns the answer span found in the best chunk.

<!-- TODO: confirm this matches the implementation, and name the model you use, e.g. deepset/roberta-large-squad2 -->

## Getting started

### Prerequisites

- JDK 25
- Internet access on the **first run**: DJL downloads the model and the PyTorch native libraries, then caches them under `~/.djl.ai`
- Enough disk space and RAM for the model (large QA models are around 1.4 GB on disk)

Maven does not need to be installed, since the wrapper is included.

### Run

```bash
git clone https://github.com/kokonick/doc-query.git
cd doc-query
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

Then open <http://localhost:8080>.

The first start can take a while because of the model and native library downloads. Later starts are much faster.

If Java 25 prints warnings about native access, you can silence them with:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="--enable-native-access=ALL-UNNAMED"
```

### Build a runnable jar

```bash
./mvnw clean package
java -jar target/doc-query-0.0.1-SNAPSHOT.jar
```

## Usage

<!-- TODO: describe the actual flow, for example:
1. Open the home page.
2. Upload a .docx file.
3. Type a question and submit.
4. Read the answer.
-->

## Configuration

Settings live in `src/main/resources/application.properties` (or `.yml`).

<!-- TODO: document the properties that matter, such as the server port, SQLite file location, model URL, chunk size, upload size limit -->

## Project structure

```
doc-query
├── .mvn/wrapper     Maven wrapper
├── src/main         Application source and resources
├── mvnw, mvnw.cmd   Maven wrapper scripts
└── pom.xml          Dependencies and build configuration
```

<!-- TODO: expand src/main with the main packages and what each one does -->

## Limitations

- **Extractive answers only.** The model returns a span copied from your document. It does not summarize or reason across sections.
- **Factual questions work best.** Questions like "what is the name of the Instructor?" or "who signed the contract?" are a good fit. Open-ended "explain why" questions are not.
- **Wording matters.** Retrieval depends on the question sharing words with the text.
- **Word content outside the main body** (text boxes, headers, footers, footnotes) may not be extracted.

## Troubleshooting

**`FileNotFoundException` for `libdjl_torch.so` with a PyTorch version that doesn't match DJL**

DJL is trying to download a native library for a PyTorch version that was never published for your DJL version. Check for:

- a `PYTORCH_VERSION` environment variable or `-DPYTORCH_VERSION=...` JVM argument
- an old PyTorch artifact on the classpath (`./mvnw dependency:tree | grep -i -E "torch|djl"`)
- a stale cache: delete `~/.djl.ai/pytorch` and retry

**First request is very slow**

The model is loaded and warmed up on first use. Subsequent requests are faster.

## Contributing

Issues and pull requests are welcome.

## License

<!-- TODO: choose a license and add a LICENSE file. Until then, all rights are reserved by default. -->
