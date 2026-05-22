package com.tielu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tielu.common.base.exception.BusinessException;
import com.tielu.common.base.exception.ErrorCode;
import com.tielu.common.security.util.JwtUtil;
import com.tielu.user.dto.request.AddPassengerRequest;
import com.tielu.user.dto.request.LoginRequest;
import com.tielu.user.dto.request.RegisterRequest;
import com.tielu.user.dto.response.LoginResponse;
import com.tielu.user.entity.Passenger;
import com.tielu.user.entity.User;
import com.tielu.user.mapper.PassengerMapper;
import com.tielu.user.mapper.UserMapper;
import com.tielu.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PassengerMapper passengerMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        LambdaQueryWrapper<User> usernameQuery = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(usernameQuery) > 0) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXIST);
        }

        LambdaQueryWrapper<User> phoneQuery = new LambdaQueryWrapper<User>()
                .eq(User::getPhone, request.getPhone());
        if (userMapper.selectCount(phoneQuery) > 0) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_REGISTERED);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(query);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setPhone(user.getPhone());
        return response;
    }

    @Override
    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, User user) {
        user.setId(userId);
        user.setPassword(null);
        userMapper.updateById(user);
    }

    @Override
    public List<Passenger> getPassengers(Long userId) {
        LambdaQueryWrapper<Passenger> query = new LambdaQueryWrapper<Passenger>()
                .eq(Passenger::getUserId, userId)
                .orderByAsc(Passenger::getCreateTime);
        return passengerMapper.selectList(query);
    }

    @Override
    @Transactional
    public void addPassenger(Long userId, AddPassengerRequest request) {
        LambdaQueryWrapper<Passenger> query = new LambdaQueryWrapper<Passenger>()
                .eq(Passenger::getUserId, userId);
        long count = passengerMapper.selectCount(query);
        if (count >= 15) {
            throw new BusinessException(ErrorCode.PASSENGER_LIMIT_REACHED);
        }

        Passenger passenger = new Passenger();
        passenger.setUserId(userId);
        passenger.setPassengerName(request.getPassengerName());
        passenger.setPassengerIdCard(request.getPassengerIdCard());
        passenger.setPassengerType(request.getPassengerType() != null ? request.getPassengerType() : 0);
        passenger.setPhone(request.getPhone());
        passengerMapper.insert(passenger);
    }

    @Override
    @Transactional
    public void deletePassenger(Long userId, Long passengerId) {
        Passenger passenger = passengerMapper.selectById(passengerId);
        if (passenger == null) {
            throw new BusinessException(ErrorCode.PASSENGER_NOT_EXIST);
        }
        if (!passenger.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        passengerMapper.deleteById(passengerId);
    }
}
