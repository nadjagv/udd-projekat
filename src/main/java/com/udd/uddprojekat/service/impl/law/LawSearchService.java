package com.udd.uddprojekat.service.impl.law;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.util.ObjectBuilder;
import com.udd.uddprojekat.exceptionhandling.exception.MalformedQueryException;
import com.udd.uddprojekat.indexmodel.LawIndex;
import com.udd.uddprojekat.service.SearchService;

import java.util.List;
import java.util.function.Function;

import com.udd.uddprojekat.util.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.PhraseQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

@Service("LawSearchService")
@RequiredArgsConstructor
public class LawSearchService implements SearchService<LawIndex> {

    private final ElasticsearchOperations elasticsearchTemplate;

    @Override
    public Page<LawIndex> simpleSearch(List<String> keywords, List<String> fieldNames, Pageable pageable, boolean isPhraseSearch) {
        var searchQueryBuilder =
                new NativeQueryBuilder().withQuery(
                        isPhraseSearch
                                ? QueryBuilder.buildPhraseQuery(String.join(" ", keywords), fieldNames)
                                : QueryBuilder.buildSimpleSearchQuery(keywords, fieldNames))
                        .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }

    @Override
    public Page<LawIndex> advancedSearch(List<String> expression, Pageable pageable) {

        var searchQueryBuilder =
                new NativeQueryBuilder().withQuery(QueryBuilder.buildAdvancedSearchQuery(expression))
                        .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }



    private Page<LawIndex> runQuery(NativeQuery searchQuery) {

        var searchHits = elasticsearchTemplate.search(searchQuery, LawIndex.class,
                IndexCoordinates.of("law_index"));

        var searchHitsPaged = SearchHitSupport.searchPageFor(searchHits, searchQuery.getPageable());

        return (Page<LawIndex>) SearchHitSupport.unwrapSearchHits(searchHitsPaged);
    }
}
