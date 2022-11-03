package com.work.covid19apiv2.controllers;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.work.covid19apiv2.EmailSenderService;
import com.work.covid19apiv2.model.Log;
import com.work.covid19apiv2.model.User;
import com.work.covid19apiv2.service.LogService;
import com.work.covid19apiv2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/covid-19/api/v2")
@CrossOrigin("*")
public class UserController {

    //declaration to use the userService to perform functions on the firebase database
    public UserService userService;

    //declaration to use the email service here
    @Autowired
    EmailSenderService senderService;

    public Log logActivity;

    //store all logs made
    @Autowired
    LogService logService;


    public UserController(UserService userService){
        this.userService = userService;
    }

    Map<String, String> locationDetails = getLocation();

    //add new user
    @Operation(summary="Sign Up user", description = "Adds a new user to the app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New user added Successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "User not successfully added",
                    content = @Content)})
    @PostMapping("/user/sign-up")
    public ResponseEntity<?> createUser(@RequestBody User user) throws InterruptedException, ExecutionException {

        sendNewUserEmail(user.getEmail(), user.getName(), "Have a great day");
        //set date and time the record was created
        LocalDateTime created_at = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = created_at.format(formatter).toString();

        String[] getDeviceDetails = getDeviceAndIp();
        String logId = generateLogId();

        logActivity = new Log(logId,"sign up, added new user","successful", getDeviceDetails[0], getDeviceDetails[1],locationDetails.get("country"), date);
        logService.createLog(logActivity);
        return userService.createUser(user);
    }

//    @Operation(summary="User Login details", description = "Login user with these details")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "User login Successfully",
//                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
//            @ApiResponse(responseCode = "404", description = "User login not successful",
//                    content = @Content)})
//    @GetMapping("/user/login")
//    public ResponseEntity<String> userLogin (@RequestParam String email, @RequestBody String password) throws InterruptedException, ExecutionException {
////        return userService.createUser(user);
//        return ResponseEntity.ok(email + " and the password: "+ password) ;
//    }

    //add user details by document id: change to use only email to get user details
    @Operation(summary="Get user details using email", description = "Get User Details using user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "no data found",
                    content = @Content)})
    @GetMapping("/user/get-user-details")
    public ResponseEntity<?> getUser(@RequestParam String email){
        //set date and time the record was created
        LocalDateTime created_at = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = created_at.format(formatter).toString();

        String[] getDeviceDetails = getDeviceAndIp();
        String logId = generateLogId();

        logActivity = new Log(logId,"get user details","successful", getDeviceDetails[0], getDeviceDetails[1],locationDetails.get("country"), date);
        logService.createLog(logActivity);
        return userService.getUser(email);
    }

    //get list of users and details in the system
    @Operation(summary ="Get all users", description = "Get all users and their details from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "No user add yet",
                    content = @Content)})
    @GetMapping("/user/get-all-users")
    public ResponseEntity<?> getUsers(){
        //set date and time the record was created
        LocalDateTime created_at = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = created_at.format(formatter).toString();

        String[] getDeviceDetails = getDeviceAndIp();
        String logId = generateLogId();
        logActivity = new Log(logId,"get user details","successful", getDeviceDetails[0], getDeviceDetails[1], locationDetails.get("country"), date);
        logService.createLog(logActivity);

        return userService.getAllUsers();
    }
    //update user details
    @Operation(summary="Update user details", description = "Update user details with passed payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "Update not successful",
                    content = @Content)})
    @PutMapping("/user/update-user-details")
    public ResponseEntity<?> updateUser(@RequestBody User user) {

        //set date and time the record was created
        LocalDateTime created_at = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = created_at.format(formatter).toString();

        String[] getDeviceDetails = getDeviceAndIp();
        String logId = generateLogId();
        logActivity = new Log(logId,"updated user details","successful", getDeviceDetails[0], getDeviceDetails[1], locationDetails.get("country"), date);
        logService.createLog(logActivity);

        return userService.updateUser(user);
    }

    //Deleted user details using the id passed
    @Operation(summary="Delete user", description = "Removes user detail from the system using the document id passed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @DeleteMapping("/user/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String email){
        //set date and time the record was created
        LocalDateTime created_at = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = created_at.format(formatter).toString();

        String[] getDeviceDetails = getDeviceAndIp();
        String logId = generateLogId();
        logActivity = new Log(logId,"user details deleted by email","successful", getDeviceDetails[0], getDeviceDetails[1], locationDetails.get("country"), date);
        logService.createLog(logActivity);

        return userService.deleteUser(email);
    }

    //test if the application is working
    @Operation(summary="Test the app", description = "Just to check if the api is working")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test successful",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "Test not successful",
                    content = @Content)})
    @GetMapping("/test-app")
    public ResponseEntity<String> testGetEndpoint(){
        return ResponseEntity.ok("Test Get Endpoint is working");
    }


    public void sendNewUserEmail(String toEmail, String userFirstName, String goodWish){

        //commented for debugging purposes
        // toEmail = "joshuaamarfio1@gmail.com";

        //subject of the email
        String subject="Covid Test App";

        //stuff for check in
        //user name, time of check in and process

        //message of the email
        String body = "Hello " +userFirstName+", "+
                //end the line
                "\nYour details have be added the to Covid-19 test system, "+
                "\nyour result will be sent to you as soon as it is ready."+
                //end the line
                "\n\n"
                +goodWish+"!!!";

        senderService.sendEmail(toEmail, subject, body);
    }

    public String[] getDeviceAndIp(){
        String[] result = new String[2];
        try{
            InetAddress ip = InetAddress.getLocalHost();
            String ipAddress = ip.getHostAddress();
            String device = ip.getHostName();

            result[0] = device;
            result[1] = ipAddress;
        } catch(Exception ex){
            ex.getMessage();
        }

        return result;

    }


    public String generateLogId(){
        int recordNumber = 0;
        try{

            recordNumber = getNoOfRecords() + 1;
            System.out.println("The new number is "+ recordNumber);

        } catch(Exception ex){
            ex.getMessage();
        }
        return "act_"+recordNumber;
    }

    public int getNoOfRecords() throws Exception {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        Iterable<DocumentReference> documentReference = dbFirestore.collection("logs").listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();

        List<Log> logList = new ArrayList<>();
        Log logActivity = null;

        int count = 0;

        while(iterator.hasNext()){

            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();
            logActivity = document.toObject(Log.class);
            count++;
            logList.add(logActivity);
        }

        return count;
    }

    public Map getLocation(){
        String url = "http://ip-api.com/json";
        RestTemplate restTemplate = new RestTemplate();
        Object locationDetails = restTemplate.getForObject(url, Object.class);
        Object country = locationDetails.getClass();
//        HashMap<String> locationDetails = new HashMap<String>();
        Map<String, String> location = (Map)locationDetails;
        String countryName = location.get("country");
        return location;
    }

}
