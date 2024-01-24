package com.example.hadapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientAvailableEkgResponse {
    private Long ecgId;
    private Date recordDate;

}
