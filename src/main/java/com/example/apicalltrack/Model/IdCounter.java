package com.example.apicalltrack.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "counters")
public class IdCounter {

    @Id
    private String id;
    private Long seq;

    public IdCounter() {}

    public IdCounter(String id, Long seq) {
        this.id = id;
        this.seq = seq;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }
}
