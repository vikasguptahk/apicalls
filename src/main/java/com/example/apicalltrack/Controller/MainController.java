package com.example.apicalltrack.Controller;

import com.example.apicalltrack.Model.apicalls;
import com.example.apicalltrack.Repository.apiiRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MainController {
    @Autowired
    apiiRepo apiirepo;

    @GetMapping("/proxy")
    public ResponseEntity<String> proxyRequest(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://example.com";
        return restTemplate.getForEntity(url, String.class);
    }
    @PostMapping("/addapicalls")
    public void addapicalls(@RequestBody apicalls apicalls1){
        apiirepo.save(apicalls1);
    }
}
