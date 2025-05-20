package it.unical.tickettwo.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersAccountsDTO {
    private long id;
    private String username;
    private String role;
    private String accessType;

    public UsersAccountsDTO(long id, String username, String role, String accessType) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.accessType = accessType;
    }

}
