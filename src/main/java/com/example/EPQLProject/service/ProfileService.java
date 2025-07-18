package com.example.EPQLProject.service;

import com.example.EPQLProject.dto.ProfileDto;
import com.example.EPQLProject.entity.ProfileEntity;
import com.example.EPQLProject.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
  private final ProfileRepository profileRepository;
  private final EmailService emailService;

  public ProfileDto registerProfile(ProfileDto profileDTO){
      ProfileEntity newProfile = toEntity(profileDTO);
      newProfile.setActivationToken(UUID.randomUUID().toString());
      newProfile = profileRepository.save(newProfile);
      String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + newProfile.getActivationToken();
      String subject = "Activate your Money Manager Account";
      String body = "Click on the following link to activate your account: " + activationLink;
      emailService.sendEmail(newProfile.getEmail(),subject,body);
      return toDto(newProfile);
  }
  public ProfileEntity toEntity(ProfileDto profileDto){
      return ProfileEntity.builder()
              .id(profileDto.getId())
              .fullName(profileDto.getFullName())
              .email(profileDto.getEmail())
              .password(profileDto.getPassword())
              .profileImageUrl(profileDto.getProfileImageUrl())
              .createdAt(profileDto.getCreatedAt())
              .updatedAt(profileDto.getUpdatedAt())
              .build();
  }
public ProfileDto toDto(ProfileEntity profileEntity){
      return ProfileDto.builder()
              .id(profileEntity.getId())
              .fullName(profileEntity.getFullName())
              .email(profileEntity.getEmail())
              .profileImageUrl(profileEntity.getProfileImageUrl())
              .createdAt(profileEntity.getCreatedAt())
              .updatedAt((profileEntity.getUpdatedAt()))
              .build();
}

public boolean activateProfile(String activationToken){
      return profileRepository.findByActivationToken(activationToken).map(profile -> {
          profile.setIsActive(true);
          profileRepository.save(profile);
          return true;
      }).orElse(false);
}
}
