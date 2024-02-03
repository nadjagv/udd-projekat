package com.udd.uddprojekat.controller.law;

import com.udd.uddprojekat.dto.DocumentFileDTO;
import com.udd.uddprojekat.dto.DocumentFileResponseDTO;
import com.udd.uddprojekat.service.IndexingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/law/index")
public class LawIndexController {

    private final IndexingService indexingService;

    public LawIndexController(@Qualifier("LawIndexingService") IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentFileResponseDTO addDocumentFile(
            @ModelAttribute DocumentFileDTO documentFile) {
        var serverFilename = indexingService.indexDocument(documentFile.file());
        return new DocumentFileResponseDTO(serverFilename);
    }
}


