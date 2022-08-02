package com.work.covid19apiv2.controllers;

import com.work.covid19apiv2.EmailSenderService;
import com.work.covid19apiv2.model.User;
import com.work.covid19apiv2.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/covid-19/api/v2")
public class UserController {

    //declaration to use the userService to perform functions on the firebase database
    public UserService userService;

    //declaration to use the email service here
    @Autowired
    EmailSenderService senderService;


    public UserController(UserService userService){
        this.userService = userService;
    }

    //add new user
    @Operation(summary="Sign Up user", description = "Adds a new user to the app")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New user added Successfully",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "User not successfully added",
                    content = @Content)})
    @PostMapping("/user/sign-up")
    public String createUser(@RequestBody User user) throws InterruptedException, ExecutionException {

        sendNewUserEmail(user.getEmail(), user.getName(), "Have a great day");
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
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping("/user/get-user-details")
    public User getUser(@RequestParam String email) throws InterruptedException, ExecutionException {
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
    public List<User> getUsers() throws ExecutionException, InterruptedException{
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
    public String updateUser(@RequestBody User user) throws InterruptedException, ExecutionException{
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
    public String deleteUser(@RequestParam String email){
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
}
