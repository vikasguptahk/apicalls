package com.example.apicalltrack.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class apicalls {
    @Id
    private Integer id;
    private String requestUrl;
    private String requestheaders;
    private String requestPayloads;
    private String responsePayloads;
    private String responseHeaders;
    private Integer responseStatusCode;
    private LocalDateTime time;
}
