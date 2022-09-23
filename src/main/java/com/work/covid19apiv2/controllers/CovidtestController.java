package com.work.covid19apiv2.controllers;


import com.work.covid19apiv2.EmailSenderService;
import com.work.covid19apiv2.model.Covidtest;
import com.work.covid19apiv2.service.CovidTestService;
import com.work.covid19apiv2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/covid-19/api/v2")
public class CovidtestController {


    //use this service to get test details for covid
    public CovidTestService covidTestService;

    //declaration to use the email service here
    @Autowired
    EmailSenderService senderService;


//    public CovidtestController(UserService userService){
//        this.userService = userService;
//    }
    public CovidtestController(CovidTestService covidTestService){this.covidTestService = covidTestService;}


    @Operation(summary="Get covid tests", description = "Get a list of covid tests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the tests",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Covidtest.class))}),
            @ApiResponse(responseCode = "404", description = "Tests not found",
                    content = @Content)})
    @GetMapping("/covidtests/get-all-tests")
    public List<Covidtest> getAllCovidTests() throws ExecutionException, InterruptedException,Exception {
        int number  = getNumberOfRecords();
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
    public String createTest(@RequestBody Covidtest covidtest) throws InterruptedException, ExecutionException {
        String userEmail = covidtest.getEmail();
        covidtest.setEmail(userEmail);

        covidtest.setCovidresult(covidResult(covidtest.getTemperature()));

        //code to get user name by using the email entered from the users table
        String username = covidTestService.getUsername(userEmail);

        sendCovidTestEmail(userEmail, username,covidtest.getCovidresult(), "Have a great day");
        return covidTestService.createCovidTest(covidtest);
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

}
