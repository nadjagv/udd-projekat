package com.udd.uddprojekat.respository;

import com.udd.uddprojekat.model.ContractTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<ContractTable, Integer> {

}
