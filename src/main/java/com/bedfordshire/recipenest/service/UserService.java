package com.bedfordshire.recipenest.service;

import com.bedfordshire.recipenest.dto.user.PublicChefProfileResponse;
import com.bedfordshire.recipenest.dto.user.UserResponse;
import com.bedfordshire.recipenest.dto.user.UserUpdateRequest;
import com.bedfordshire.recipenest.entity.User;
import com.bedfordshire.recipenest.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class UserService {

    // Repository for user data
    private final UserRepository userRepository;

    // Service for S3 file storage
    private final S3Service s3Service;

    public UserService(UserRepository userRepository, S3Service s3Service){
        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    public UserResponse updateMyProfile(UserUpdateRequest request, String currentUserEmail){
        // Find the currently logged-in user by email
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Update only the text fields that were actually provided in the request
        if(request.firstName() != null){
            currentUser.setFirstName(request.firstName());
        }

        if(request.lastName() != null){
            currentUser.setLastName(request.lastName());
        }

        if(request.location() != null){
            currentUser.setLocation(request.location());
        }

        if(request.bio() != null){
            currentUser.setBio(request.bio());
        }

        if(request.cuisineSpeciality() != null){
            currentUser.setCuisineSpeciality(request.cuisineSpeciality());
        }

        // Save the updated user and convert it to response DTO
        User updatedUser = userRepository.save(currentUser);
        return UserResponse.from(updatedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getMyProfile(String currentUserEmail){
        // Find the currently authenticated user
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Convert the user entity to response dto
        return UserResponse.from(currentUser);
    }

    public UserResponse uploadProfilePhoto(MultipartFile file, String currentUserEmail){
        // Find the currently logged-in user
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Keep the old image url so it can be deleted after the new one succeeds
        String oldImageUrl = currentUser.getProfilePhoto();

        // Upload the new profile image to S3 first
        String newImageUrl = s3Service.uploadFile(file, "users");

        // Update the user with the new image url
        currentUser.setProfilePhoto(newImageUrl);

        // Save the new profile image reference
        User updatedUser = userRepository.save(currentUser);

        // Delete the previous image only after the new one has been saved
        if(oldImageUrl != null && !oldImageUrl.isBlank()){
            s3Service.deleteFileByUrl(oldImageUrl);
        }

        // Return the updated user as response dto
        return UserResponse.from(updatedUser);
    }

    @Transactional(readOnly = true)
    public PublicChefProfileResponse getPublicChefProfile(Long userId){
        // Find the requested user by id
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Chef not found"));

        // Convert the user entity to a public chef profile dto
        return PublicChefProfileResponse.from(user);
    }
}