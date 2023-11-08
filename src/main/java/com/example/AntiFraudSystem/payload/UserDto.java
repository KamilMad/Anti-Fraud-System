package com.example.AntiFraudSystem.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    private String username;

    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return id.equals(userDto.id) && name.equals(userDto.name) && username.equals(userDto.username) && role.equals(userDto.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, username, role);
    }
}
