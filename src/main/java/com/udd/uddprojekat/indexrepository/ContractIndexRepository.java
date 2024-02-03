package com.udd.uddprojekat.indexrepository;

import com.udd.uddprojekat.indexmodel.ContractIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractIndexRepository extends ElasticsearchRepository<ContractIndex, String> {

}
