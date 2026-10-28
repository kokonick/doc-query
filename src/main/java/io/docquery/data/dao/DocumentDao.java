package io.docquery.data.dao;

import io.docquery.data.model.Document;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DocumentDao {

    private final JdbcClient jdbcClient;

    public DocumentDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Integer create(Document document) {
        String sql = "INSERT INTO document(title, content, file_name) VALUES (:title, :content, :file_name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql(sql)
                .param("title", document.title())
                .param("file_name", document.fileName())
                .param("content", document.content())
                .update(keyHolder, "id");

        return keyHolder.getKeyAs(Integer.class);
    }

    public List<Document> findAll() {
        return jdbcClient.sql("SELECT id, title, file_name, created_at, NULL AS content FROM document ")
                .query(Document.class)
                .list();
    }

    public Optional<Document> findById(Integer docId) {
        return jdbcClient.sql("SELECT id, title, file_name as fileName, created_at as createdAt, NULL AS content FROM document WHERE id = :id")
                .param("id", docId)
                .query(Document.class)
                .optional();
    }

    public Optional<Document> findDocumentById(Integer docId) {
        return jdbcClient.sql("SELECT id, title, content, file_name as fileName, created_at as createdAt FROM document WHERE id = :id")
                .param("id", docId)
                .query(Document.class)
                .optional();
    }


    public int deleteByIds(List<Integer> ids) {
        return jdbcClient.sql("DELETE FROM document WHERE id IN (:ids)")
                .param("ids", ids)
                .update();
    }
}
