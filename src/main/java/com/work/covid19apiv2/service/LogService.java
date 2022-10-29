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

    //create response
    ApiResponse response;

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

    //method to get all logs done so far
    public ResponseEntity<ApiResponse> getAllLogs()  {
        response = new ApiResponse(statusCode, message, data);

        try{

            Firestore dbFirestore = FirestoreClient.getFirestore();

            Iterable<DocumentReference> documentReference = dbFirestore.collection("logs").listDocuments();
            Iterator<DocumentReference> iterator = documentReference.iterator();

            List<Log> logList = new ArrayList<>();
            Log log = null;

            while(iterator.hasNext()){
                DocumentReference documentReference1 = iterator.next();
                ApiFuture<DocumentSnapshot> future = documentReference1.get();
                DocumentSnapshot document = future.get();

                log = document.toObject(Log.class);
                logList.add(log);
            }

            if(logList.size() > 0){
                response = new ApiResponse(statusCode, message, logList);
            }else{
                response = new ApiResponse("404", "no data found", data);
            }
            return ResponseEntity.ok(response);
        } catch(Exception ex){
            response = new ApiResponse("404", ex.getMessage(), data);
            return ResponseEntity.ok(response);
        }
    }


    //generate new document id



}
