package io.docquery.web.dto;

import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class DocumentForm {

//    @NotBlank(message = "Name required")
    @Size(min = 5, max = 25, message = "Requires between 5 - 30 characters")
    private String title;
//    @NotNull(message = "Please choose a file.")
    private MultipartFile file;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
