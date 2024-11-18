package com.app.domain.user.service;

import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.global.config.auth.dto.OAuthAttributes;
import com.app.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 처음보는 방식의 로직. 단일쿼리 후 map. entity가 null이면 map은 실행되지 않음
    public User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }

    // initDb용
    public User save(User user) {
        return userRepository.save(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }
}
