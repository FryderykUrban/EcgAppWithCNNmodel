package com.example.hadapp.repository;

import com.example.hadapp.model.ECGRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ECGRecordRepository extends JpaRepository<ECGRecord, Long> {
    boolean existsByPatientIdAndRecordDate(Long id, Date recordDate);
    List<ECGRecord> findAllByPatientId(Long id);

}
