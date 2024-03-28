package com.example.apicalltrack.Repository;

import com.example.apicalltrack.Model.apicalls;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface apiiRepo extends MongoRepository<apicalls,Integer> {

}
