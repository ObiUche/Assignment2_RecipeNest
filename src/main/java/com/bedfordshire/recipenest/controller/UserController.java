package com.bedfordshire.recipenest.controller;

import com.bedfordshire.recipenest.dto.user.PublicChefProfileResponse;
import com.bedfordshire.recipenest.dto.user.UserResponse;
import com.bedfordshire.recipenest.dto.user.UserUpdateRequest;
import com.bedfordshire.recipenest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    // Service that contains the user business logic
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication){
        // Return the currently logged-in users profile details
        return ResponseEntity.ok(userService.getMyProfile(authentication.getName()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication
    ){
        // Update only the current logged-in user's safe text profile fields
        UserResponse response = userService.updateMyProfile(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> uploadProfilePhoto(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ){
        // Upload a new profile image for the current logged-in user
        // and return the updated user profile dto
        UserResponse response = userService.uploadProfilePhoto(file, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/public")
    public ResponseEntity<PublicChefProfileResponse> getPublicChefProfile(@PathVariable Long id){
        // Return the public profile details for one chef
        return ResponseEntity.ok(userService.getPublicChefProfile(id));
    }
}