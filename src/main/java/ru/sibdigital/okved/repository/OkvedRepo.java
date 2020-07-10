package ru.sibdigital.okved.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.okved.model.Okved;

import java.util.List;

@Repository
public interface OkvedRepo extends JpaRepository<Okved, Integer> {

    @Modifying
    @Query(value = "update okved set status = 0", nativeQuery = true)
    void resetStatus();

    @Modifying
    @Query(value = "update okved set ts_kind_name = to_tsvector('russian',kind_name), " +
            "ts_description = to_tsvector('russian', description)", nativeQuery = true)
    void setTsVectors();

    Okved findByKindCode(String kindCode);

    @Query(value = "select * from okved where kind_code like %:text% " +
            "or lower(kind_name) like %:text% " +
//            "or ts_kind_name @@ to_tsquery('russian', :text) " +
//            "or ts_description @@ to_tsquery('russian', :text) " +
            "order by kind_code", nativeQuery = true)
    List<Okved> findBySearchText(@Param("text") String text);
}
