package com.example.EPQLProject.controller;

import com.example.EPQLProject.dto.ProfileDto;
import com.example.EPQLProject.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto>registerProfile(@RequestBody ProfileDto profileDto){
        ProfileDto registeredProfile = profileService.registerProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }
    @GetMapping("/activate")
    public ResponseEntity<String>activateProfile(@RequestParam String token){
        boolean isActivated= profileService.activateProfile(token);
        if(isActivated){
            return ResponseEntity.ok("Profile activated successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or Already used");
        }
    }

}
