package com.example.hadapp.controller;

import com.example.hadapp.model.Patient;
import com.example.hadapp.model.User;
import com.example.hadapp.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.hadapp.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/patients")
public class PatientController {

    private final PatientService patientService;
    private final UserService userService;

    public PatientController(PatientService patientService, UserService userService) {
        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long patientId) {
        return Optional.ofNullable(patientService.getPatientById(patientId))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        Optional<User> user = userService.getUserById(1L);
        patient.setUser(user.get());

        Patient savedPatient = patientService.addPatient(patient);
        return ResponseEntity.ok(savedPatient);
    }

    @GetMapping("/by-doctor-username/{username}")
    public ResponseEntity<List<Patient>> getPatientsByDoctorUsername(@PathVariable String username) {
        List<Patient> patients = patientService.getPatientsByDoctorUsername(username);
        if (patients.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patients);
    }


}
