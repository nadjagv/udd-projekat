package com.udd.uddprojekat.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

@Service
public interface SearchService<T>{

    SearchPage<T> simpleSearch(List<String> keywords, List<String> fieldNames, Pageable pageable, boolean isPhraseSearch);

    SearchPage<T> advancedSearch(List<String> expression, Pageable pageable);
}
