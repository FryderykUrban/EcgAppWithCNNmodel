package com.example.hadapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "ECGRecords")
public class ECGRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ecg_id")
    private Long ecgId;

    @ManyToOne
    @JoinColumn(name="patient_id")
    private Patient patient;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "record_date")
    private Date recordDate;

    @Lob
    @Column(name = "ecg_signal_blob", columnDefinition="MEDIUMBLOB")
    private byte[] ecgSignalBlob;


    @Lob
    @Column(name = "samples_blob", columnDefinition="MEDIUMBLOB")
    private byte[] samplesBlob;

    @Column(name = "analysis_results", columnDefinition="TEXT")
    private String analysisResults;

    @Column(name = "arrhythmia_intervals", columnDefinition="TEXT")
    private String arrhythmiaIntervals;
}

