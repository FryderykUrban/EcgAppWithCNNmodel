package com.example.hadapp.repository;

import com.example.hadapp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient,Long> {

    @Query("SELECT p FROM Patient p WHERE p.user.username = :username")
    List<Patient> findAllByDoctorUsername(@Param("username") String email);
}
