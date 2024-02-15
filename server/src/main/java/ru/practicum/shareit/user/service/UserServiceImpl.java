package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public User getUserById(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public User saveUser(UserDto userDto) {
        userValidate(userDto);
        User user = repository.save(UserMapper.toUser(userDto));
        if (userValidateEmail(userDto, user.getId())) {
            deleteUser(user.getId());
            throw new ResponseStatusException(HttpStatus.valueOf(409));
        }
        return repository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserDto userDto) {
        userValidate(userDto, userId);
        User user = repository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        User save = repository.save(user);
        return save;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        repository.deleteById(userId);
    }

    public void userValidate(UserDto user) {
        // Валидация имени
        if ((user.getName() == null || user.getName().isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Имя не может быть Null или пустым");
        }

        // Валидация email
        if (user.getEmail() != null) {
            // Валидация формата email при наличии значения
            if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неправильный формат email");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.valueOf(400), "Email не может быть пустым");
        }
    }

    public void userValidate(UserDto user, Long userId) {
        // Валидация email
        if (user.getEmail() != null) {
            // Валидация формата email при наличии значения
            if (!user.getEmail().contains("@") || !user.getEmail().contains(".")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неправильный формат email");
            }

            if (repository.findAll().stream()
                    .filter(user1 -> !user1.getId().equals(userId))
                    .anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()))) {
                throw new ResponseStatusException(HttpStatus.valueOf(409), "такой email уже есть");
            }
        }
    }

    public boolean userValidateEmail(UserDto user, long idUser) {
        // Проверка на дубликат email при обновлении пользователя
        return repository.findAll().stream()
                .filter(user1 -> !user1.getId().equals(idUser))
                .anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()));
    }
}
