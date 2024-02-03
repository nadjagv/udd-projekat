package com.udd.uddprojekat.service.impl.law;

import com.udd.uddprojekat.indexmodel.DummyIndex;
import com.udd.uddprojekat.indexmodel.LawIndex;
import com.udd.uddprojekat.indexrepository.LawIndexRepository;
import com.udd.uddprojekat.model.DummyTable;
import com.udd.uddprojekat.model.LawTable;
import com.udd.uddprojekat.respository.LawRepository;
import com.udd.uddprojekat.service.FileService;
import com.udd.uddprojekat.service.IndexingService;
import com.udd.uddprojekat.util.IndexingUtil;
import jakarta.transaction.Transactional;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service(value = "LawIndexingService")
@RequiredArgsConstructor
public class LawIndexingService implements IndexingService {

    private final LawIndexRepository lawIndexRepository;

    private final LawRepository lawRepository;

    private final FileService fileService;

    private final IndexingUtil indexingUtil;

    @Override
    @Transactional
    public String indexDocument(MultipartFile documentFile) {
        var newEntity = new LawTable();
        var newIndex = new LawIndex();

        var title = Objects.requireNonNull(documentFile.getOriginalFilename()).split("\\.")[0];
        newIndex.setTitle(title);
        newEntity.setTitle(title);

        var documentContent = indexingUtil.extractDocumentContent(documentFile);
        System.out.println(documentContent);
        if (indexingUtil.detectLanguage(documentContent).equals("SR")) {
            newIndex.setContentSr(documentContent);
        } else {
            newIndex.setContentEn(documentContent);
        }
        newEntity.setTitle(title);

        var serverFilename = fileService.store(documentFile, UUID.randomUUID().toString());
        newIndex.setServerFilename(serverFilename);
        newEntity.setServerFilename(serverFilename);

        newEntity.setMimeType(indexingUtil.detectMimeType(documentFile));
        var savedEntity = lawRepository.save(newEntity);

        newIndex.setDatabaseId(savedEntity.getId());
        lawIndexRepository.save(newIndex);

        return serverFilename;
    }
}
