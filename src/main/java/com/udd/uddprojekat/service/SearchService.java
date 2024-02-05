package com.udd.uddprojekat.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface SearchService<T>{

    Page<T> simpleSearch(List<String> keywords, List<String> fieldNames, Pageable pageable, boolean isPhraseSearch);

    Page<T> advancedSearch(List<String> expression, Pageable pageable);
}
