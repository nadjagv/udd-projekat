package com.udd.uddprojekat.indexrepository;

import com.udd.uddprojekat.indexmodel.LawIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawIndexRepository extends ElasticsearchRepository<LawIndex, String> {

}
