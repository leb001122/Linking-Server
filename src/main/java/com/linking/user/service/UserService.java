package com.linking.user.service;

import com.linking.participant.domain.Participant;
import com.linking.participant.persistence.ParticipantRepository;
import com.linking.project.domain.Project;
import com.linking.user.dto.*;
import com.linking.user.persistence.UserMapper;
import com.linking.user.persistence.UserRepository;
import com.linking.global.exception.BadRequestException;
import com.linking.global.message.ErrorMessage;
import com.linking.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final ParticipantRepository participantRepository;
    private final PasswordEncoder bCryptPasswordEncoder;


    public UserDetailedRes addUser(UserSignUpReq userSignUpReq)
            throws DataIntegrityViolationException {

        User user = userMapper.toEntity(userSignUpReq);
        user.hashPassword(bCryptPasswordEncoder);

        return userMapper.toDto(userRepository.save(user));
    }

    public boolean isUniqueEmail(UserEmailVerifyReq emailReq){
        return userRepository.findUserByEmail(emailReq.getEmail()).isPresent();
    }

    public List<UserDetailedRes> getUsersByPartOfEmail(UserEmailReq userEmailReq)
        throws NoSuchElementException{
        List<User> userList = userRepository.findUsersByPartOfEmail(userEmailReq.getPartOfEmail());
        if (!userList.isEmpty() && userEmailReq.getProjectId() != -1L) {
            List<Participant> possibleParticipants =
                    participantRepository.findByProject(new Project(userEmailReq.getProjectId()));
            if (!possibleParticipants.isEmpty())
                userList = userList.stream().filter(
                        user -> possibleParticipants.stream().noneMatch(
                                        participant -> user.getUserId().equals(participant.getUser().getUserId())))
                        .collect(Collectors.toList());
        }
        return userMapper.toDto(userList);
    }

    public Optional<UserDetailedRes> getUserById(Long userId)
            throws NoSuchElementException{
        return Optional.ofNullable(userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(NoSuchElementException::new));
    }

    public UserDetailedRes getUserWithEmailAndPw(UserSignInReq userSignInReq) {

        User user = userRepository.findUserByEmail(userSignInReq.getEmail())
                .orElseThrow(NoSuchElementException::new);

        if (bCryptPasswordEncoder.matches(userSignInReq.getPassword(), user.getPassword())) {
            return userMapper.toDto(user);
        } else {
            throw new BadRequestException(ErrorMessage.DO_NOT_MATCH_PW);
        }
    }

    public void deleteUser(Long userId)
            throws EmptyResultDataAccessException, DataIntegrityViolationException {
        userRepository.deleteById(userId);
    }
}