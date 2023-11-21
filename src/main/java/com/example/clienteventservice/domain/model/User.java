package com.example.clienteventservice.domain.model;


import com.example.clienteventservice.domain.dto.UserDtoClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private String profile;
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    public static UserDtoClient toDto(UserRepresentation userRepresentation, String url) {
        return new UserDtoClient(
                UUID.fromString(userRepresentation.getId()),
                userRepresentation.getFirstName(),
                userRepresentation.getLastName(),
                userRepresentation.getUsername(),
                userRepresentation.getEmail(),
                userRepresentation.getAttributes().get("phoneNumber").get(0),
                url+userRepresentation.getAttributes().get("profile").get(0),
                LocalDateTime.parse(userRepresentation.getAttributes().get("createdDate").get(0)),
                LocalDateTime.parse(userRepresentation.getAttributes().get("lastModified").get(0))
        );
    }
}

