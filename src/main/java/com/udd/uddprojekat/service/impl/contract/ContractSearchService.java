package com.udd.uddprojekat.service.impl.contract;

import com.udd.uddprojekat.exceptionhandling.exception.MalformedQueryException;
import com.udd.uddprojekat.indexmodel.ContractIndex;
import com.udd.uddprojekat.service.SearchService;

import java.util.List;

import com.udd.uddprojekat.util.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

@Service("ContractSearchService")
@RequiredArgsConstructor
public class ContractSearchService implements SearchService<ContractIndex> {

    private final ElasticsearchOperations elasticsearchTemplate;

    @Override
    public Page<ContractIndex> simpleSearch(List<String> keywords, List<String> fieldNames, Pageable pageable) {
        var searchQueryBuilder =
                new NativeQueryBuilder().withQuery(QueryBuilder.buildSimpleSearchQuery(keywords, fieldNames))
                        .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }

    @Override
    public Page<ContractIndex> advancedSearch(List<String> expression, Pageable pageable) {
        if (expression.size() != 3) {
            throw new MalformedQueryException("Search query malformed.");
        }

        String operation = expression.get(1);
        expression.remove(1);
        var searchQueryBuilder =
                new NativeQueryBuilder().withQuery(QueryBuilder.buildAdvancedSearchQuery(expression, operation))
                        .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }


    private Page<ContractIndex> runQuery(NativeQuery searchQuery) {

        var searchHits = elasticsearchTemplate.search(searchQuery, ContractIndex.class,
                IndexCoordinates.of("contract_index"));

        var searchHitsPaged = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());

        return (Page<ContractIndex>) SearchHitSupport.unwrapSearchHits(searchHitsPaged);
    }
}

