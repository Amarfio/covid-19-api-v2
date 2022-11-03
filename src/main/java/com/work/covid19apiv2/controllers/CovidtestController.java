package com.work.covid19apiv2.controllers;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.work.covid19apiv2.EmailSenderService;
import com.work.covid19apiv2.model.Covidtest;
import com.work.covid19apiv2.model.Log;
import com.work.covid19apiv2.service.CovidTestService;
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
import java.net.UnknownHostException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/covid-19/api/v2")
@CrossOrigin("*")
public class CovidtestController {


    //use this service to get test details for covid
    public CovidTestService covidTestService;

    //declaration to use the email service here
    @Autowired
    EmailSenderService senderService;

    public Log logActivity;

    //store all logs made
    @Autowired
     LogService logService;

//    public CovidtestController(UserService userService){
//        this.userService = userService;
//    }

    Map<String, String> locationDetails = getLocation();
    public CovidtestController(CovidTestService covidTestService){this.covidTestService = covidTestService;}
//    public CovidtestController(LogService logService){this.logService = logService;}


    @Operation(summary="Get covid tests", description = "Get a list of covid tests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "data found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Covidtest.class))}),
            @ApiResponse(responseCode = "404", description = "no data found",
                    content = @Content)})
    @GetMapping("/covidtests/get-all-tests")
    public ResponseEntity<?> getAllCovidTests() {

        //set date and time the record was created
        LocalDateTime created_at = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = created_at.format(formatter).toString();

        String[] getDeviceDetails = getDeviceAndIp();
        String logId = generateLogId();
        System.out.println(logId);
        logActivity = new Log(logId,"get all covid tests","successful", getDeviceDetails[0], getDeviceDetails[1], locationDetails.get("country"), date);


        logService.createLog(logActivity);
        return covidTestService.getAllTests();
    }


    @Operation(summary="Add covid test", description = "Add a new covid test")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test added successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Covidtest.class))}),
            @ApiResponse(responseCode = "404", description = "Test was not added successfully",
                    content = @Content)})
    @PostMapping("/covidtests/add-new-covidtest")
    //method to get temperature and email to determine covid state of user
    public ResponseEntity<?> createTest(@RequestBody Covidtest covidtest){
        String userEmail = covidtest.getEmail();
        covidtest.setEmail(userEmail);

        covidtest.setCovidresult(covidResult(covidtest.getTemperature()));

        //code to get user name by using the email entered from the users table
        String username = covidTestService.getUsername(userEmail);

        sendCovidTestEmail(userEmail, username,covidtest.getCovidresult(), "Have a great day");

        //set date and time the record was created
        LocalDateTime created_at = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = created_at.format(formatter).toString();

        String[] getDeviceDetails = getDeviceAndIp();

        String country=locationDetails.get("country");
        logActivity = new Log(generateLogId(),"new covid test added","successful", getDeviceDetails[0], getDeviceDetails[1], country,  date);


        logService.createLog(logActivity);
        return covidTestService.createCovidTest(covidtest);
    }

//    @GetMapping("/alldetails")
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

    //method for creating email content for users
    public void sendCovidTestEmail(String toEmail, String userFirstName, String process, String goodWish){

        //commented for debugging purposes
        // toEmail = "joshuaamarfio1@gmail.com";

        //subject of the email
        String subject="Covid Test Update";

        //stuff for check in
        //user name, time of check in and process

        //message of the email
        String body = "Hello " +userFirstName+", "+
                //end the line
                "\nYour covid test result indicates that "+process+
                //end the line
                "\n\n"
                +goodWish+"!!!";

        senderService.sendEmail(toEmail, subject, body);
    }

    private String covidResult(double temperature) {
        while (temperature > 39) {

            if (temperature >= 40) {
                return "A Suspected COVID Case";
            }

        }

        return "Normal Temperature";
    }

    public int getNumberOfRecords() throws  Exception{

        System.out.println("the number of records is "+ covidTestService.countRecords());
        return covidTestService.countRecords();
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


}
