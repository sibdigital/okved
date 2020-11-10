package ru.sibdigital.okved.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.sibdigital.okved.model.Okved;

import java.util.List;
import java.util.UUID;

@Repository
public interface OkvedRepo extends JpaRepository<Okved, Integer> {

    @Modifying
    @Query(value = "update okved set status = 0", nativeQuery = true)
    void resetStatus();

    @Modifying
    @Query(value = "update okved set ts_kind_name = to_tsvector('russian',kind_name), " +
            "ts_description = to_tsvector('russian', description)", nativeQuery = true)
    void setTsVectors();

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE okved\nSET ts_kind_name = to_tsvector('russian',kind_name),\n    ts_description = to_tsvector('russian', description)\nWHERE id = :id",
            nativeQuery = true
    )
    void setTsVectorsById(@Param("id") UUID id);

    Okved findByKindCode(String kindCode);

    Okved findByPath(String path);

    @Query(
            value = "select * from okved where kind_code like %:text% or lower(kind_name) like %:text% order by kind_code",
            nativeQuery = true
    )
    List<Okved> findBySearchText(@Param("text") String text);

    @Query(
            value = "select * from okved where version = :version order by kind_code",
            nativeQuery = true
    )
    List<Okved> findOkvedsByVersion(@Param("version") String version);

    Okved findOkvedById(@Param("id") UUID id);
}
