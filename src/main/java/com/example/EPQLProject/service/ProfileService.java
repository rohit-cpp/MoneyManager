package com.example.EPQLProject.service;

import com.example.EPQLProject.dto.AuthDto;
import com.example.EPQLProject.dto.ProfileDto;
import com.example.EPQLProject.entity.ProfileEntity;
import com.example.EPQLProject.repository.ProfileRepository;
import com.example.EPQLProject.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
  private final ProfileRepository profileRepository;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;


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
              .password(passwordEncoder.encode(profileDto.getPassword()))
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
public boolean isAccountActive(String email){
      return profileRepository.findByEmail(email)
              .map(ProfileEntity::getIsActive).orElse(false);
}
 public ProfileEntity getCurrentProfile(){
     Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
//     String email = authentication.getName();
     return profileRepository.findByEmail(authentication.getName())
             .orElseThrow(()-> new UsernameNotFoundException("Profile not found with email: " + authentication.getName()));
 }
 public ProfileDto getPublicProfile(String email){
      ProfileEntity currentUser = null;
      if (email == null){
          getCurrentProfile();
      }
      else {
          currentUser = profileRepository.findByEmail(email)
                  .orElseThrow(()->new UsernameNotFoundException("Profile not found with email: "+ email));
      }
      return ProfileDto.builder()
              .id(currentUser.getId())
              .fullName(currentUser.getFullName())
              .email(currentUser.getEmail())
              .profileImageUrl(currentUser.getProfileImageUrl())
              .createdAt(currentUser.getCreatedAt())
              .updatedAt(currentUser.getCreatedAt())
              .build();
 }
 public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto){
      try{
         authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
       String token = jwtUtil.generateToken(authDto.getEmail());
         return Map.of(
                 "token", token,
                 "user", getPublicProfile(authDto.getEmail())
         );
      } catch (Exception e) {
          throw new RuntimeException("Invalid email or password");
      }
 }

}
