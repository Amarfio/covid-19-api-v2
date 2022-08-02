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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<Covidtest> getAllCovidTests() throws ExecutionException, InterruptedException {
        return covidTestService.getAllTests();
    }



}
