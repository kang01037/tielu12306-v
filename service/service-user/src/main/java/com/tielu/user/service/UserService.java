package com.tielu.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tielu.user.dto.request.AddPassengerRequest;
import com.tielu.user.dto.request.LoginRequest;
import com.tielu.user.dto.request.RegisterRequest;
import com.tielu.user.dto.response.LoginResponse;
import com.tielu.user.entity.Passenger;
import com.tielu.user.entity.User;

import java.util.List;

public interface UserService extends IService<User> {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    User getUserInfo(Long userId);

    void updateUserInfo(Long userId, User user);

    List<Passenger> getPassengers(Long userId);

    void addPassenger(Long userId, AddPassengerRequest request);

    void deletePassenger(Long userId, Long passengerId);
}
