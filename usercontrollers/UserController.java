package com.salesSavvy.app.usercontrollers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salesSavvy.app.entities.User;
import com.salesSavvy.app.entities.userdao;
import com.salesSavvy.app.userservices.UserServiceContract;

@CrossOrigin(origins = "http://localhost:5174")
@RestController

@RequestMapping("/api/users")
public class UserController {
	private UserServiceContract userService;

	public UserController(UserServiceContract userService) {
		super();
		this.userService = userService;
	}
	
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(Map.of("message", "User registered successfully", "user", new userdao(registeredUser.getUserId(), registeredUser.getUsername(), registeredUser.getEmail(), registeredUser.getRole().toString())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
		
	
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody User user) {
//        try {
//            User registeredUser = userService.registerUser(user);
//            return ResponseEntity.ok(Map.of("message", "User registered successfully", "user", registeredUser));
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        }
//    }
}
