package com.udd.uddprojekat.service.impl.contract;

import com.udd.uddprojekat.exceptionhandling.exception.MalformedQueryException;
import com.udd.uddprojekat.indexmodel.ContractIndex;
import com.udd.uddprojekat.service.SearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.udd.uddprojekat.util.Constants;
import com.udd.uddprojekat.util.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

@Service("ContractSearchService")
@RequiredArgsConstructor
public class ContractSearchService implements SearchService<ContractIndex> {

    private final ElasticsearchOperations elasticsearchTemplate;

    @Override
    public SearchPage<ContractIndex> simpleSearch(List<String> keywords, List<String> fieldNames, Pageable pageable, boolean isPhraseSearch) {
        var highlightFields = new ArrayList<HighlightField>();
        for (var fieldName: fieldNames) {
            var hf = new HighlightField(fieldName);
            highlightFields.add(hf);
        }
        var highlight = new Highlight(highlightFields);
        var highliQuery = new HighlightQuery(highlight, ContractIndex.class);

        var searchQueryBuilder =
                new NativeQueryBuilder().withQuery(
                                isPhraseSearch
                                        ? QueryBuilder.buildPhraseQuery(String.join(" ", keywords), fieldNames)
                                        : QueryBuilder.buildSimpleSearchQuery(keywords, fieldNames))
                        .withHighlightQuery(highliQuery)
                        .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }

    @Override
    public SearchPage<ContractIndex> advancedSearch(List<String> expression, Pageable pageable) {
        var fieldNames = List.of(Constants.CONTENT_SR_FIELD_NAME, Constants.CONTENT_EN_FIELD_NAME);
        var highlightFields = new ArrayList<HighlightField>();
        for (var fieldName: fieldNames) {
            var hf = new HighlightField(fieldName);
            highlightFields.add(hf);
        }
        var highlight = new Highlight(highlightFields);
        var highliQuery = new HighlightQuery(highlight, ContractIndex.class);

        var searchQueryBuilder =
                new NativeQueryBuilder().withQuery(QueryBuilder.buildAdvancedSearchQuery(expression))
                        .withHighlightQuery(highliQuery)
                        .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }


    private SearchPage<ContractIndex> runQuery(NativeQuery searchQuery) {

        var searchHits = elasticsearchTemplate.search(searchQuery, ContractIndex.class,
                IndexCoordinates.of("contract_index"));

        for (var hit : searchHits) {
            Map<String, List<String>> highlightFields = hit.getHighlightFields();
            var highlight = highlightFields.get("title");
        }

        SearchPage<ContractIndex> searchHitsPaged = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());

        return searchHitsPaged;
    }
}

