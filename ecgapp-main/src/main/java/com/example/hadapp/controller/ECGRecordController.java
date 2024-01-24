package com.example.hadapp.controller;

import com.example.hadapp.dto.EcgChartData;
import com.example.hadapp.dto.PatientAvailableEkgResponse;
import com.example.hadapp.model.ECGRecord;
import com.example.hadapp.service.ECGRecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ecg-records")
public class ECGRecordController {

    private final ECGRecordService ecgRecordService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    public ECGRecordController(ECGRecordService ecgRecordService) {
        this.ecgRecordService = ecgRecordService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadEcgFile(@RequestParam("file") MultipartFile file, @RequestParam("patientId") Long patientId, @RequestParam("recordDateString") String recordDateString) {
        try {
            Date recordDate = new Date();

            ecgRecordService.uploadEcgCsv(file.getInputStream(), patientId, recordDate);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{ecgId}/chart-data")
    public ResponseEntity<List<EcgChartData>> getEcgChartData(@PathVariable Long ecgId, @RequestParam(defaultValue = "0.0") Float from, @RequestParam(defaultValue = "50.0") Float to) {
        try {
            List<EcgChartData> data = ecgRecordService.getEcgChartData(ecgId, from, to);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/patient")
    public List<PatientAvailableEkgResponse> getPatientAvailableEkg(@RequestParam Long patientId) {
        return ecgRecordService.getPatientAvailableEkg(patientId);
    }

    @PostMapping("/{ecgId}/analyze")
    public ResponseEntity<?> analyzeEcg(@PathVariable Long ecgId) {
        try {
            ECGRecord ecgRecord = ecgRecordService.getEcgRecord(ecgId);
            byte[] ecgSignalBlob = ecgRecord.getEcgSignalBlob();
            List<Float> signal = ecgRecordService.convertBlobToList(ecgSignalBlob);

            Map<String, List<Float>> requestBody = Map.of("signal", signal);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:5000/analyze", requestBody, String.class);

            // Interpretacja odpowiedzi i zapis do bazy
            Map<String, Object> responseMap = objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
            String diagnosis = (String) responseMap.get("diagnosis");
            String arrhythmiaIntervals = objectMapper.writeValueAsString(responseMap.get("arrhythmia_intervals"));

            ecgRecord.setAnalysisResults(diagnosis);
            ecgRecord.setArrhythmiaIntervals(arrhythmiaIntervals);
            ecgRecordService.saveEcgRecord(ecgRecord);

            return ResponseEntity.ok().build(); // Zwraca tylko potwierdzenie pomyślnej analizy
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Wystąpił błąd podczas analizy: " + e.getMessage());
        }
    }


    private String convertBlobToFlaskInputFormat(byte[] blob) {
        return Base64.getEncoder().encodeToString(blob);
    }

    @GetMapping("/{ecgId}/analysis-results")
    public ResponseEntity<?> getEcgAnalysisResults(@PathVariable Long ecgId) throws JsonProcessingException {
        ECGRecord ecgRecord = ecgRecordService.getEcgRecord(ecgId);
        if (ecgRecord.getAnalysisResults() != null && ecgRecord.getArrhythmiaIntervals() != null) {
            List<List<Double>> intervals = objectMapper.readValue(
                    ecgRecord.getArrhythmiaIntervals(),
                    new TypeReference<List<List<Double>>>() {}
            );

            List<List<Double>> filteredIntervals = intervals.stream()
                    .filter(interval -> interval.size() == 2 && !interval.get(0).equals(interval.get(1)))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "diagnosis", ecgRecord.getAnalysisResults(),
                    "arrhythmiaIntervals", filteredIntervals
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
