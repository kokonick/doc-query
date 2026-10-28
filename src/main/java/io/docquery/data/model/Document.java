package io.docquery.data.model;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record Document(Integer id, String title, String content, String fileName, String createdAt) {
}
