package io.docquery.service;

import ai.djl.ModelException;
import ai.djl.huggingface.translator.QuestionAnsweringTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.modality.nlp.qa.QAInput;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import io.docquery.web.dto.QueryForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DocumentQueryService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentQueryService.class);

    public String askQuestion(QueryForm queryForm){
        // 1. Define the content context and your question
        String content = queryForm.getContent();
        String question = queryForm.getQuery();

        QAInput input = new QAInput(question, content);
        Criteria<QAInput, String> criteria = Criteria.builder()
                .setTypes(QAInput.class, String.class)
                .optModelUrls("djl://ai.djl.huggingface.pytorch/deepset/roberta-large-squad2")
                .optEngine("PyTorch")
                .optTranslatorFactory(new QuestionAnsweringTranslatorFactory())
                .optArgument("translatorFactory", new QuestionAnsweringTranslatorFactory())
                .build();

        // 3. Load the model and execute the predictor
        try (ZooModel<QAInput, String> model = criteria.loadModel();
             Predictor<QAInput, String> predictor = model.newPredictor()) {

             String answer = predictor.predict(input);
//            logger.info("Answer: {}",answer);

            return answer;
        } catch (ModelException | IOException | TranslateException e) {
            throw new RuntimeException(e);
        }
    }

}
