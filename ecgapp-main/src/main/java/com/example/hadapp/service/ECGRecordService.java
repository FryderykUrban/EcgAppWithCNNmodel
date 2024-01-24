package com.example.hadapp.service;

import com.example.hadapp.dto.EcgChartData;
import com.example.hadapp.dto.PatientAvailableEkgResponse;
import com.example.hadapp.model.ECGRecord;
import com.example.hadapp.repository.ECGRecordRepository;
import com.example.hadapp.repository.PatientRepository;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ECGRecordService {

    private final ECGRecordRepository ecgRecordRepository;
    private final PatientRepository patientRepository;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    public ECGRecordService(ECGRecordRepository ecgRecordRepository, PatientRepository patientRepository) {
        this.ecgRecordRepository = ecgRecordRepository;
        this.patientRepository = patientRepository;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource[] resources = resolver.getResources("classpath*:ECGData/*.csv");
        for (Resource resource : resources) {
            String filename = resource.getFilename();

            String[] parts = filename.replace(".csv", "").split("_");
            Long patientId = Long.parseLong(parts[0]);
            Date recordDate = null;
            try {
                recordDate = dateFormat.parse(parts[1]);
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }

            boolean recordExists = ecgRecordRepository.existsByPatientIdAndRecordDate(patientId, recordDate);

            if (!recordExists) {
                uploadEcgCsv(resource.getInputStream(), patientId, recordDate);
            }

        }
    }

    public void uploadEcgCsv(InputStream resource, Long patientId, Date recordDate) throws IOException {

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8));
             org.apache.commons.csv.CSVParser csvParser = new org.apache.commons.csv.CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<CSVRecord> csvRecords = csvParser.getRecords();
            List<Float> mlIIList = new ArrayList<>();
            List<Float> secondsList = new ArrayList<>();

            for (CSVRecord csvRecord : csvRecords) {
                mlIIList.add(Float.parseFloat(csvRecord.get("MLII")));
                secondsList.add(Float.parseFloat(csvRecord.get("seconds")));
            }

            byte[] mlIIBlob = convertListToBlob(mlIIList);
            byte[] secondsBlob = convertListToBlob(secondsList);

            ECGRecord ecgRecord = new ECGRecord();
            ecgRecord.setPatient(patientRepository.findById(patientId).orElseThrow());
            ecgRecord.setRecordDate(recordDate);
            ecgRecord.setEcgSignalBlob(mlIIBlob);
            ecgRecord.setSamplesBlob(secondsBlob);

            ecgRecordRepository.save(ecgRecord);
        }
    }

    private byte[] convertListToBlob(List<Float> list) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(list.size() * Float.BYTES);
        for (Float value : list) {
            byteBuffer.putFloat(value);
        }
        return byteBuffer.array();
    }

    public List<EcgChartData> getEcgChartData(Long ecgId, Float from, Float to) {
        ECGRecord ecgRecord = ecgRecordRepository.findById(ecgId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with id: " + ecgId));

        List<Float> mlIIValues = convertBlobToList(ecgRecord.getEcgSignalBlob());
        List<Float> secondsValues = convertBlobToList(ecgRecord.getSamplesBlob());
        List<Float> chosenSeconds = secondsValues.stream().filter(value -> value >= from && value <= to).toList();

        List<EcgChartData> chartDataList = new ArrayList<>();
        for (int i = 0; i < chosenSeconds.size(); i++) {
            EcgChartData chartData = new EcgChartData();
            chartData.setMlII(mlIIValues.get(i));
            chartData.setSeconds(chosenSeconds.get(i));
            chartDataList.add(chartData);
        }
        return chartDataList;
    }

    public List<Float> convertBlobToList(byte[] blob) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(blob);
        List<Float> list = new ArrayList<>();
        while (byteBuffer.hasRemaining()) {
            list.add(byteBuffer.getFloat());
        }
        return list;
    }

    public List<PatientAvailableEkgResponse> getPatientAvailableEkg(Long patientId) {
        return ecgRecordRepository.findAllByPatientId(patientId).stream()
                .map(ecgRecord -> PatientAvailableEkgResponse.builder()
                        .ecgId(ecgRecord.getEcgId())
                        .recordDate(ecgRecord.getRecordDate())
                        .build())
                .toList();
    }

    public ECGRecord getEcgRecord(Long ecgId) {
        return ecgRecordRepository.findById(ecgId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono rekordu EKG z id: " + ecgId));
    }

    public void saveEcgRecord(ECGRecord ecgRecord) {
        ecgRecordRepository.save(ecgRecord);
    }
}
