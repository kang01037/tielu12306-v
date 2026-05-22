package com.tielu.user.controller;

import com.tielu.common.base.result.Result;
import com.tielu.user.dto.request.AddPassengerRequest;
import com.tielu.user.dto.request.LoginRequest;
import com.tielu.user.dto.request.RegisterRequest;
import com.tielu.user.dto.response.LoginResponse;
import com.tielu.user.entity.Passenger;
import com.tielu.user.entity.User;
import com.tielu.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("userId") Long userId) {
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestHeader("userId") Long userId, @RequestBody User user) {
        userService.updateUserInfo(userId, user);
        return Result.success();
    }

    @GetMapping("/passengers")
    public Result<List<Passenger>> getPassengers(@RequestHeader("userId") Long userId) {
        List<Passenger> passengers = userService.getPassengers(userId);
        return Result.success(passengers);
    }

    @PostMapping("/passengers")
    public Result<Void> addPassenger(@RequestHeader("userId") Long userId,
                                     @Valid @RequestBody AddPassengerRequest request) {
        userService.addPassenger(userId, request);
        return Result.success();
    }

    @DeleteMapping("/passengers/{id}")
    public Result<Void> deletePassenger(@RequestHeader("userId") Long userId,
                                        @PathVariable Long id) {
        userService.deletePassenger(userId, id);
        return Result.success();
    }
}
