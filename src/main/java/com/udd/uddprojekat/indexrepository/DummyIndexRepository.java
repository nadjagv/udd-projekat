package com.udd.uddprojekat.indexrepository;

import com.udd.uddprojekat.indexmodel.DummyIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyIndexRepository
    extends ElasticsearchRepository<DummyIndex, String> {
}
