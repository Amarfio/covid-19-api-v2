package com.work.covid19apiv2.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.work.covid19apiv2.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    //method that creates a new user
    public String createUser(User user) throws ExecutionException, InterruptedException {

        Firestore dbFirestore = FirestoreClient.getFirestore();

        //set date and time the record was created
        LocalDateTime created_at = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String date = created_at.format(formatter).toString();
        user.setCreated_date(date);

        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getEmail()).set(user);

        return "New user created on "+ collectionsApiFuture.get().getUpdateTime().toString();
    }

    //method gets the user details by the specified document id
    public User getUser (String email) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference =dbFirestore.collection("users").document(email);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        User user;

        //return the document details if it exists
        if(document.exists()){
            user = document.toObject(User.class);
            return user;
        }

        return null;
    }

    //method to get all user details saved in the database
    public List<User> getAllUsers() throws ExecutionException, InterruptedException{

        Firestore dbFirestore = FirestoreClient.getFirestore();

        Iterable<DocumentReference> documentReference = dbFirestore.collection("users").listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();

        List<User> userList = new ArrayList<>();
        User user = null;

        while(iterator.hasNext()){
            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();

            user=document.toObject(User.class);
            userList.add(user);
        }

        return userList;
    }

    //update the user details
    public String updateUser(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        String email = user.getEmail();
        //get user details using the email from the database
        DocumentReference documentReference =dbFirestore.collection("users").document(email);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        //retain user's created date value
        User userAlready = document.toObject(User.class);

        user.setCreated_date(userAlready.getCreated_date());

        //set date and time the record was updated
        LocalDateTime created_at = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String date = created_at.format(formatter).toString();
        user.setUpdated_date(date);

        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getEmail()).set(user);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

//    public String loginUser(String email, String password) throws ExecutionException, InterruptedException {
//
//    }

    //delete user details
    public String deleteUser(String email){
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFirestore.collection("users").document(email).delete();

        return "Successfully deleted " + email;
    }
}
