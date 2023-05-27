package com.work.covid19apiv2.controllers;

import com.work.covid19apiv2.EmailSenderService;
import com.work.covid19apiv2.model.Covidtest;
import com.work.covid19apiv2.model.Log;
import com.work.covid19apiv2.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("covid-19/api/v2")
@CrossOrigin("*")
public class LogController {

    //use this service to get the log details
//    @Autowired
    public LogService logService;

    public LogController(LogService logService){
        this.logService = logService;
    }

    @Operation(summary="Get application logs", description = "Get a list of logs made using the application")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "000", description = "data found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Log.class))}),
            @ApiResponse(responseCode = "404", description = "no data found",
                    content = @Content)})
//    @GetMapping("/logs/get-all-logs")
    @GetMapping("/logs")
    public ResponseEntity<?> getAllLogs(){
        return logService.getAllLogs();
    }

}
