package com.work.covid19apiv2.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.work.covid19apiv2.model.Covidtest;
import com.work.covid19apiv2.model.User;
import org.springframework.stereotype.Service;

import javax.swing.text.Document;
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
}
