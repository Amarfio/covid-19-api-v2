package com.work.covid19apiv2.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.work.covid19apiv2.controllers.ApiResponse;
import com.work.covid19apiv2.model.Covidtest;
import com.work.covid19apiv2.model.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class LogService {
        String statusCode = "000";
        String message = "default message";
        Object data = null;

    public void createLog(Log newLog){
        System.out.println(newLog.getId());
        try{
//            newLog.setId(generateLogId());
            System.out.println(newLog.getId());
            Firestore dbFirestore = FirestoreClient.getFirestore();
            System.out.println("here we dey");
            ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("logs").document(newLog.getId()).set(newLog);
            System.out.println(collectionApiFuture.get().getUpdateTime().toString());

        }catch (Exception ex){
            ex.getMessage();
        }
    }


    //generate new document id



}
