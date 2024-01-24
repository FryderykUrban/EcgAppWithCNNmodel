package com.example.hadapp.service;

import com.example.hadapp.model.Patient;
import com.example.hadapp.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient getPatientById(Long patientId) {
        return patientRepository.findById(patientId).orElse(null);
    }

    public Patient addPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public List<Patient> getPatientsByDoctorUsername(String username) {
        return patientRepository.findAllByDoctorUsername(username);
    }
}
