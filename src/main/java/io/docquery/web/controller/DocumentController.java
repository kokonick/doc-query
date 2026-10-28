package io.docquery.web.controller;

import io.docquery.data.model.Document;
import io.docquery.data.model.DocumentBuilder;
import io.docquery.service.DocumentQueryService;
import io.docquery.service.DocumentService;
import io.docquery.util.DocQuerytUtil;
import io.docquery.web.dto.DocumentForm;
import io.docquery.web.dto.QueryForm;
import jakarta.validation.Valid;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;
    private final DocumentQueryService documentQueryService;

    private Path uploadDir;

    /**
     * @param documentService
     */
    public DocumentController(DocumentService documentService, DocumentQueryService documentQueryService) {
        this.documentService = documentService;
        this.documentQueryService = documentQueryService;
    }

	@GetMapping(value = {"/allDocuments" , "index", "/"})
	public String documents(Model model) {
        final List<Document> documents = documentService.findAllDocuments();
        model.addAttribute("documents", documents);
		return "document-list";
	}

    @PostMapping("/allDocuments")
    public String deleteDocuments(@RequestParam List<Integer> ids) {
        if(ids !=null && !ids.isEmpty()){
            documentService.deleteByIds(ids);
        }

        return "redirect:/allDocuments";
    }

    @GetMapping("/document")
    public String addDocument(Model model) {
        model.addAttribute("documentForm", new DocumentForm());
        return "add-document";
    }

    @PostMapping(value = "/document")
    public String addNewDocument(@ModelAttribute @Valid DocumentForm documentForm,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "add-document";
        }

        var file = documentForm.getFile();

        // validation: Check if file is empty
        if (file == null || !StringUtils.hasText(file.getName())) {
            bindingResult.rejectValue("file", "error.documentForm", "Please select a non-empty file.");
            return "add-document";
        } else if (!isValidDocument(file)) {
            bindingResult.rejectValue("file", "error.documentForm", "Please only Microsoft word document (.docx)");
            return "add-document";
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (fileName.contains("..")) {
                bindingResult.rejectValue("file", "error.documentForm", "Invalid file path pattern.");
                return "add-document";
            }

            if (uploadDir == null) {
                final Path autoDeletingTempDir = DocQuerytUtil.createAutoDeletingTempDir("doc-query-dir");
                uploadDir =  autoDeletingTempDir.toAbsolutePath().normalize();
            }

            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Document document = processWordDocument(documentForm, filePath);
            documentService.saveNewDocument(document);

            redirectAttributes.addFlashAttribute("successMessage", "File uploaded successfully: " + fileName);
            return "redirect:/document";
        } catch (IOException e) {
            logger.error("error while processing file", e);
            redirectAttributes.addFlashAttribute("errorMeesage", "Could not save file:  " + fileName);
            return "redirect:/document";
        }

    }

    private static Document processWordDocument(DocumentForm documentForm, Path savedFile) {
        final String content = extractContent(savedFile);
        return DocumentBuilder.builder()
                .title(documentForm.getTitle())
                .fileName(savedFile.getFileName().toString())
                .content(content)
                .build();
    }

    private static String extractContent(Path savedFile) {
        try (FileInputStream fis = new FileInputStream(savedFile.toString());
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {

             return extractor.getText();
        } catch (IOException e) {
            logger.error("Error extracting MS Word content", e);
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/queryDocument/{id}")
    public String queryDocument(@PathVariable("id") Integer documentId, Model model) {
        if (!model.containsAttribute("queryForm")) {
            final Optional<Document> document = documentService.findById(documentId);

            final QueryForm queryForm = document.map(doc -> {
                QueryForm form = new QueryForm();
                form.setDocumentId(doc.id());
                form.setTitle(doc.title());

                return form;
            }).orElse(new QueryForm());

            model.addAttribute("queryForm", queryForm);
        }

        return "query-document";
    }

    @PostMapping("/queryDocument")
    public String queryDocument(@ModelAttribute @Valid QueryForm queryForm,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        final Integer documentId = queryForm.getDocumentId();
        Document document = null;
        if (documentId != null) {
            document =  documentService.findDocumentById(documentId).orElse(null);
        }

        if (document == null) {
            return "redirect:/allDocuments";
        }

        queryForm.setContent(document.content());
        queryForm.setTitle(document.title());

        final String answer = documentQueryService.askQuestion(queryForm);
        queryForm.setContent(null);

        redirectAttributes.addFlashAttribute("successMessage", answer)
                .addAttribute("id", queryForm.getDocumentId())
                .addFlashAttribute("queryForm", queryForm);

        return "redirect:/queryDocument/{id}";
    }

    @ModelAttribute("pageTitle")
    public String pageTitle() {
        return "page-title.document-list";
    }

    private static boolean isValidDocument(MultipartFile file) {
        String contentType = file.getContentType();

        return contentType != null && (
//                        contentType.equals("application/msword") || // For old .doc files
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") // For .docx
                || file.getName().endsWith(".docx")
        );
    }

}
