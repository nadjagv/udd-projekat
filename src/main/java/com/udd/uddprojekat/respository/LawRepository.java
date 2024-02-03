package com.udd.uddprojekat.respository;

import com.udd.uddprojekat.model.LawTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawRepository extends JpaRepository<LawTable, Integer> {

}
