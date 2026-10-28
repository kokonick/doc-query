package io.docquery.service;

import io.docquery.data.dao.DocumentDao;
import io.docquery.data.model.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DocumentService {

    private DocumentDao documentDao;

    public DocumentService(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    @Transactional
    public Integer saveNewDocument(Document document) {
        return documentDao.create(document);
    }

    public List<Document> findAllDocuments() {
        return documentDao.findAll();
    }

    public Optional<Document> findById(Integer docId) {
        return documentDao.findById(docId);
    }

    public Optional<Document> findDocumentById(Integer docId) {
        return documentDao.findDocumentById(docId);
    }

    @Transactional
    public void deleteByIds(List<Integer> ids) {
        documentDao.deleteByIds(ids);
    }
}
