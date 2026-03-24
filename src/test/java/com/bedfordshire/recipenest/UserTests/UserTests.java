package com.bedfordshire.recipenest.UserTests;


import com.bedfordshire.recipenest.entity.User;
import com.bedfordshire.recipenest.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTests {

    // Test to see if i set accountLocked
    @Test
    void isLocked_ShouldReturnTrue_WhenAccountNonLockedIsFalse(){
        User user = new User();
        user.setAccountNonLocked(false);
        user.setLockoutTime(LocalDateTime.now());

        assertFalse(user.isAccountNonLocked());

    }

    @Test
    void user_ShouldBeCreated_withValidData(){
        User user = new User("John","Doe","John@Doe.com","passwordHash");

        assertEquals("John",user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("John@Doe.com", user.getEmail());
        assertEquals("passwordHash", user.getPasswordHash());
        assertEquals(UserRole.CHEF, user.getRole());
        assertTrue(user.isActive());
        assertTrue(user.isAccountNonLocked());
        assertEquals(0, user.getFailedLoginAttempts());
        assertNull(user.getLockoutTime());

    }

    @Test
    void incrementFailedLoginAttempts_ShouldLockAccount_After5Attempts(){

        User user = new User("John","Doe","John@Doe.com","passwordHash");
        // Try 5 Failed attempts
        for(int i = 0; i < 5; i ++){
            user.incrementFailedLoginAttempts();
        }

        assertEquals(5, user.getFailedLoginAttempts());
        assertFalse(user.isAccountNonLocked());
        assertNotNull(user.getLockoutTime());
    }

    @Test
    void incrementFailedLoginAttempts_ShouldNotLock_WithLessThan5Attempts() {
        User user = new User("John", "Doe", "john@example.com", "password");

        user.incrementFailedLoginAttempts();
        user.incrementFailedLoginAttempts();

        assertEquals(2, user.getFailedLoginAttempts());
        assertTrue(user.isAccountNonLocked());
        assertNull(user.getLockoutTime());
    }


    @Test
    void getAuthorities_ShouldReturnRole_WithPrefix() {
        User user = new User();
        user.setRole(UserRole.CHEF);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertEquals(1, authorities.size());
        assertEquals("ROLE_CHEF", authorities.iterator().next().getAuthority());
    }


    @Test
    void getFullName_ShouldReturnFirstNameAndLastName() {
        User user = new User("John", "Doe", "john@example.com", "password");
        assertEquals("John Doe", user.getFullName());
    }

    @Test
    void incrementFailedLoginAttempts_ShouldNotExceedMaxAttempts() {
        User user = new User();

        // Call increment 10 times
        for(int i = 0; i < 10; i++) {
            user.incrementFailedLoginAttempts();
        }

        assertEquals(5, user.getFailedLoginAttempts()); // Should stop at 5
        assertFalse(user.isAccountNonLocked());
    }

    @Test
    void isLocked_ShouldHandleNullLockoutTime() {
        User user = new User();
        user.setAccountNonLocked(false);
        user.setLockoutTime(null);

        assertTrue(user.isLocked()); // Locked without lockout time
    }

}
