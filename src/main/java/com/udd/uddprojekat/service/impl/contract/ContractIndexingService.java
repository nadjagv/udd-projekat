package com.udd.uddprojekat.service.impl.contract;

import com.udd.uddprojekat.indexmodel.ContractIndex;
import com.udd.uddprojekat.indexrepository.ContractIndexRepository;
import com.udd.uddprojekat.model.ContractTable;
import com.udd.uddprojekat.respository.ContractRepository;
import com.udd.uddprojekat.service.FileService;
import com.udd.uddprojekat.service.IndexingService;
import com.udd.uddprojekat.util.IndexingUtil;
import jakarta.transaction.Transactional;

import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service(value = "ContractIndexingService")
@RequiredArgsConstructor
public class ContractIndexingService implements IndexingService {

    private final ContractIndexRepository contractIndexRepository;

    private final ContractRepository contractRepository;

    private final FileService fileService;

    private final IndexingUtil indexingUtil;

    private static final String UPRAVA_ZA = "Uprava za";

    private static final String NIVO_UPRAVE = "Nivo uprave:";

    @Override
    @Transactional
    public String indexDocument(MultipartFile documentFile) {
        var newEntity = new ContractTable();
        var newIndex = new ContractIndex();

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

        parseEmployeeName(newEntity, newIndex, documentContent);
        parseGovernmentName(newEntity, newIndex, documentContent);
        parseGovernmentLevel(newEntity, newIndex, documentContent);
        parseGovernmentAddress(newEntity, newIndex, documentContent);

        System.out.println(newEntity);

        var serverFilename = fileService.store(documentFile, UUID.randomUUID().toString());
        newIndex.setServerFilename(serverFilename);
        newEntity.setServerFilename(serverFilename);

        newEntity.setMimeType(indexingUtil.detectMimeType(documentFile));
        var savedEntity = contractRepository.save(newEntity);

        newIndex.setDatabaseId(savedEntity.getId());
        contractIndexRepository.save(newIndex);

        return serverFilename;
    }

    void parseEmployeeName(ContractTable contractTable, ContractIndex contractIndex, String documentContent) {
        var tokens = documentContent.split("\n");
        var employeeName = tokens[tokens.length - 3];
        if (employeeName.isBlank() || employeeName.contains("Potpisnik ugovora za klijenta")) {
            employeeName = null;
        }
        contractTable.setEmployeeName(employeeName);
        contractIndex.setEmployeeName(employeeName);
    }

    void parseGovernmentName(ContractTable contractTable, ContractIndex contractIndex, String documentContent) {
        var governmentDataBeginIndex = documentContent.indexOf(UPRAVA_ZA);
        var governmentNameBeginIndex = governmentDataBeginIndex + UPRAVA_ZA.length();
        var governmentLevelBeginIndex = documentContent.indexOf(NIVO_UPRAVE);

        var governmentName = documentContent.substring(governmentNameBeginIndex, governmentLevelBeginIndex).strip();

        contractTable.setGovernmentName(governmentName);
        contractIndex.setGovernmentName(governmentName);

    }

    void parseGovernmentLevel(ContractTable contractTable, ContractIndex contractIndex, String documentContent) {
        var governmentLevelBeginIndex = documentContent.indexOf(NIVO_UPRAVE) + NIVO_UPRAVE.length();
        var newLineAfterLevel = documentContent.indexOf("\n", governmentLevelBeginIndex);

        var governmentLevel = documentContent.substring(governmentLevelBeginIndex, newLineAfterLevel).strip();

        contractTable.setGovernmentLevel(governmentLevel);
        contractIndex.setGovernmentLevel(governmentLevel);
    }

    void parseGovernmentAddress(ContractTable contractTable, ContractIndex contractIndex, String documentContent) {
        var governmentLevelBeginIndex = documentContent.indexOf(NIVO_UPRAVE);
        var newLineAfterLevel = documentContent.indexOf("\n", governmentLevelBeginIndex);
        var monkeyInEmail = documentContent.indexOf("@", newLineAfterLevel);

        var addressDataWithEmailUsername = documentContent.substring(newLineAfterLevel, monkeyInEmail);
        var lastSpace = addressDataWithEmailUsername.lastIndexOf('\n');
        var governmentAddress = addressDataWithEmailUsername.substring(0, lastSpace).strip();

        contractTable.setGovernmentAddress(governmentAddress);
        contractIndex.setGovernmentAddress(governmentAddress);
    }


}