package com.work.covid19apiv2.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.work.covid19apiv2.model.Covidtest;
import com.work.covid19apiv2.model.User;
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

    //method to get all tests done so far
    public List<Covidtest> getAllTests() throws ExecutionException, InterruptedException {
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

        return covidtestList;
    }

    //method to add new covid test
    public String createCovidTest(Covidtest covidtest) throws InterruptedException, ExecutionException{
        //set date and time the record was created
        LocalDateTime created_at = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String date = created_at.format(formatter).toString();
        covidtest.setCreated_date(date);



        Firestore dbFirestore = FirestoreClient.getFirestore();

        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("covidtests").document(covidtest.getEmail()).set(covidtest);

        return "New created created on "+ collectionApiFuture.get().getUpdateTime().toString();
    }



    //method gets the user name by the specified email entered
    public String getUsername (String email) throws ExecutionException, InterruptedException {
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
    }
}
