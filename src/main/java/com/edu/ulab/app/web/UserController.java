package com.edu.ulab.app.web;

import com.edu.ulab.app.facade.UserDataFacade;
import com.edu.ulab.app.web.constant.WebConstant;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;

import static com.edu.ulab.app.web.constant.WebConstant.REQUEST_ID_PATTERN;
import static com.edu.ulab.app.web.constant.WebConstant.RQID;

@Slf4j
@RestController
@RequestMapping(value = WebConstant.VERSION_URL + "/user",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserDataFacade userDataFacade;

    public UserController(UserDataFacade userDataFacade) {
        this.userDataFacade = userDataFacade;
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create user book row.",
            description = "The method creates a new user with books.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {@ExampleObject(name = "A new user with books",
                                            value = """
                                                    {
                                                      "userRequest": {
                                                        "fullName": "string",
                                                        "title": "string",
                                                        "age": 0
                                                      },
                                                      "bookRequests": [
                                                        {
                                                          "title": "string",
                                                          "author": "string",
                                                          "pageCount": 0
                                                        }
                                                      ]
                                                    }"""
                                    )})),
            responses = {@ApiResponse(responseCode = "200",
                            description = """
                                    OK: the request has succeeded and has led to creation of a new user with books.
                                    
                                    Returns a user book response.""",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))})
    public UserBookResponse createUserWithBooks(@RequestBody UserBookRequest request,
                                                @RequestHeader(RQID) @Pattern(regexp = REQUEST_ID_PATTERN) final String requestId) {
        UserBookResponse response = userDataFacade.createUserWithBooks(request);
        log.info("Response with created user and his books: {}", response);
        return response;
    }


    @PutMapping(value = "/update")
    @Operation(summary = "Update user book row.",
            description = "The method updates a user with books.",
            responses = {@ApiResponse(responseCode = "200",
                            description = """
                                    OK: the request has succeeded and has led to update of a user with books.
                                    
                                    Returns a user book response.""",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request: User is not found.", content = @Content)})
    public UserBookResponse updateUserWithBooks(@RequestBody UserBookRequest request,
                                                @RequestHeader(RQID) @Pattern(regexp = REQUEST_ID_PATTERN) final String requestId) {
        UserBookResponse response = userDataFacade.updateUserWithBooks(request);
        log.info("Response with updated user and his books: {}", response);
        return response;
    }

    @GetMapping(value = "/get/{userId}")
    @Operation(summary = "Get user with books.",
            description = "The method fetches a single user with books by ID.",
            responses = {@ApiResponse(responseCode = "200",
                            description = """
                                    OK: the request has succeeded.
                                    
                                    Returns a user book response.""",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request: User is not found.", content = @Content)})
    public UserBookResponse getUserWithBooks(@PathVariable Long userId,
                                             @RequestHeader(RQID) @Pattern(regexp = REQUEST_ID_PATTERN) final String requestId) {
        UserBookResponse response = userDataFacade.getUserWithBooks(userId);
        log.info("Response with user and his books: {}", response);
        return response;
    }

    @DeleteMapping(value = "/delete/{userId}")
    @Operation(summary = "Delete user book row.",
            description = "The method deletes a user with books by ID.",
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "OK: the request has succeeded and has led to deletion of a user with books.",
                    content = @Content),
                        @ApiResponse(responseCode = "400", description = "Bad request: User is not found.", content = @Content)})
    public void deleteUserWithBooks(@PathVariable Long userId,
                                    @RequestHeader(RQID) @Pattern(regexp = REQUEST_ID_PATTERN) final String requestId) {
        userDataFacade.deleteUserWithBooks(userId);
    }
}
