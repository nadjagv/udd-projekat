package com.udd.uddprojekat.service;

import com.udd.uddprojekat.indexmodel.DummyIndex;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface SearchService<T>{

    Page<T> simpleSearch(List<String> keywords, Pageable pageable);

    Page<T> advancedSearch(List<String> expression, Pageable pageable);
}
