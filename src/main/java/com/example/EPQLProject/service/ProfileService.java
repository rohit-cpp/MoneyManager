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

  public ProfileDto registerProfile(ProfileDto profileDTO){
      ProfileEntity newProfile = toEntity(profileDTO);
      newProfile.setActivationToken(UUID.randomUUID().toString());
      newProfile = profileRepository.save(newProfile);
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

}
