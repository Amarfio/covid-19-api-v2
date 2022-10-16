package com.work.covid19apiv2.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.protobuf.Api;
import com.work.covid19apiv2.controllers.ApiResponse;
import com.work.covid19apiv2.model.Covidtest;
import com.work.covid19apiv2.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.text.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CovidTestService {

    //variable to store the number of records
    public int numberOfRecords = 0;

    //create response
    ApiResponse response;

    //response parameters
    String statusCode = "200";
    String message = "data found";
    Object data = null;

    //method to get all tests done so far
    public ResponseEntity<ApiResponse> getAllTests()  {
        response = new ApiResponse(statusCode, message, data);

        try{

            Firestore dbFirestore = FirestoreClient.getFirestore();

            Iterable<DocumentReference> documentReference = dbFirestore.collection("covidtests").listDocuments();
            Iterator<DocumentReference> iterator = documentReference.iterator();

            List<Covidtest> covidtestList = new ArrayList<>();
            Covidtest covidtest = null;

            while(iterator.hasNext()){
                DocumentReference documentReference1 = iterator.next();
                ApiFuture<DocumentSnapshot> future = documentReference1.get();
                DocumentSnapshot document = future.get();

                covidtest = document.toObject(Covidtest.class);
                covidtestList.add(covidtest);
            }

            if(covidtestList.size() > 0){
                response = new ApiResponse(statusCode, message,covidtestList);
            }else{
                response = new ApiResponse("404", "no data found", data);
            }
            return ResponseEntity.ok(response);
        } catch(Exception ex){
            response = new ApiResponse("404", ex.getMessage(), data);
            return ResponseEntity.ok(response);
        }
    }

    //method to add new covid test
    public ResponseEntity<ApiResponse> createCovidTest(Covidtest covidtest) {
        response = new ApiResponse(statusCode, message, data);
        try{

            //generate a test id
            covidtest.setTest_id(generateTestId());

            //set date and time the record was created
            LocalDateTime created_at = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date = created_at.format(formatter).toString();
            covidtest.setCreated_date(date);



            Firestore dbFirestore = FirestoreClient.getFirestore();

            ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("covidtests").document(covidtest.getTest_id()).set(covidtest);
            response = new ApiResponse(statusCode, "Test added successfully", covidtest);
            return ResponseEntity.ok(response);
        }catch(Exception ex){
            return ResponseEntity.ok(new ApiResponse("400", ex.getMessage(), data));
        }
//        return "New created created on "+ collectionApiFuture.get().getUpdateTime().toString();
    }



    //method gets the user name by the specified email entered
    public String getUsername (String email) {
        try{
            Firestore dbFirestore = FirestoreClient.getFirestore();
            DocumentReference documentReference =dbFirestore.collection("users").document(email);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();

            User user;

            //return the document details if it exists
            if(document.exists()){
                user = document.toObject(User.class);
                return user.getName();
            }

            return "user name not found";
        }catch (Exception ex){
            return ex.getMessage();
        }

    }

    //method to get the number of records in the database
    public int countRecords(){
        int count = 0;

        try{

            Firestore dbFirestore = FirestoreClient.getFirestore();

            Iterable<DocumentReference> documentReference = dbFirestore.collection("covidtests").listDocuments();
            Iterator<DocumentReference> iterator = documentReference.iterator();

            List<Covidtest> covidtestList = new ArrayList<>();
            Covidtest covidtest = null;



            while(iterator.hasNext()){

                DocumentReference documentReference1 = iterator.next();
                ApiFuture<DocumentSnapshot> future = documentReference1.get();
                DocumentSnapshot document = future.get();

                covidtest = document.toObject(Covidtest.class);
                numberOfRecords++;
                covidtestList.add(covidtest);
            }
            count = numberOfRecords;
        }catch(Exception ex ){
            System.out.println(ex);
        }



        return count;
    }

    public String generateTestId(){
        int recordNumber = countRecords() + 1;
        String result = "test_" + recordNumber;
        return result;
    }
}
