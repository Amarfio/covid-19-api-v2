package com.work.covid19apiv2.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.protobuf.Api;
import com.work.covid19apiv2.controllers.ApiResponse;
import com.work.covid19apiv2.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    ApiResponse response;

    //parameteres for the response
    String statusCode ="200";
    String message = "data found";
    Object data = null;

    //method that creates a new user
    public ResponseEntity<ApiResponse> createUser(User user) {
        response = new ApiResponse(statusCode, message, data);

        try{
            Firestore dbFirestore = FirestoreClient.getFirestore();

            //set date and time the record was created
            LocalDateTime created_at = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date = created_at.format(formatter).toString();
            user.setCreated_date(date);

            ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getEmail()).set(user);
            response = new ApiResponse(statusCode, "New user added successfully", user);
            return ResponseEntity.ok(response);
        }catch(Exception ex){
            response = new ApiResponse("400", ex.getMessage(), data);
            return ResponseEntity.ok(response);
        }
    }

    //method gets the user details by the specified document id
    public ResponseEntity<ApiResponse> getUser (String email) {
        response = new ApiResponse(statusCode, message, data);
        try{
            Firestore dbFirestore = FirestoreClient.getFirestore();
            DocumentReference documentReference =dbFirestore.collection("users").document(email);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();

            User user;

            //return the document details if it exists
            if(document.exists()){
                user = document.toObject(User.class);
                response = new ApiResponse(statusCode, message, user);
                return ResponseEntity.ok(response);
            }
            else{
                response = new ApiResponse("404", "no data found", data);
                return ResponseEntity.ok(response);
            }
        }catch(Exception ex){
                response = new ApiResponse("400", ex.getMessage(), data);
                return ResponseEntity.ok(response);
        }

    }

    //method to get all user details saved in the database
    public ResponseEntity<ApiResponse> getAllUsers(){

        response = new ApiResponse(statusCode, message, data);
        try{
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
            if(userList.size()>0){
                response = new ApiResponse(statusCode, message, userList);
            }
            else{
                response = new ApiResponse(statusCode, message, data);
            }

            return ResponseEntity.ok(response);

        }catch(Exception ex){
            return ResponseEntity.ok(new ApiResponse("400", ex.getMessage(), data));
        }
    }

    //update the user details
    public ResponseEntity<ApiResponse> updateUser(User user) {
        try{

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String date = created_at.format(formatter).toString();
            user.setUpdated_date(date);

            ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getEmail()).set(user);
            response = new ApiResponse(statusCode, "update successful", user);
            return ResponseEntity.ok(response);
        }catch(Exception ex){
            return ResponseEntity.ok(new ApiResponse("400", ex.getMessage(), data));
        }
    }

//    public String loginUser(String email, String password) throws ExecutionException, InterruptedException {
//
//    }

    //delete user details
    public ResponseEntity<ApiResponse> deleteUser(String email){

        try{
            Firestore dbFirestore = FirestoreClient.getFirestore();
            ApiFuture<WriteResult> writeResult = dbFirestore.collection("users").document(email).delete();
            response = new ApiResponse(statusCode, "user deleted successfully", email);
            return ResponseEntity.ok(response);
        }catch(Exception ex){
            return ResponseEntity.ok(new ApiResponse("400", ex.getMessage(), data));
        }

//        return "Successfully deleted " + email;
    }
}
