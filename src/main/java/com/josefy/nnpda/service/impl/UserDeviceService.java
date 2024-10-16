package com.josefy.nnpda.service.impl;

import com.josefy.nnpda.infrastructure.utils.Either;
import com.josefy.nnpda.infrastructure.utils.Status;
import com.josefy.nnpda.model.Device;
import com.josefy.nnpda.model.User;
import com.josefy.nnpda.model.UserDevice;
import com.josefy.nnpda.repository.IDeviceRepository;
import com.josefy.nnpda.repository.IUserDeviceRepository;
import com.josefy.nnpda.service.IUserDeviceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDeviceService implements IUserDeviceService {
    private final IDeviceRepository deviceRepository;
    private final IUserDeviceRepository userDeviceRepository;

    @Override
    public List<UserDevice> findAllForUser(User user) {
        return userDeviceRepository.findByUser(user);
    }

    @Override
    @Transactional
    public Either<Status, Void> unassignDeviceFromUser(String serialNumber, User user) {
        Device device = deviceRepository.findBySerialNumber(serialNumber).orElse(null);
        if (device == null) {
            return Either.left(new Status("Device with serial number " + serialNumber + " does not exist.",
                    HttpStatus.NOT_FOUND));
        }
        if (userDeviceRepository.existsByUserAndSerialNumber(user, serialNumber)) {
            userDeviceRepository.unassignDeviceFromUser(user, serialNumber);
            return Either.right(null);
        } else {
            return Either.left(new Status("Device with serial number " + serialNumber + " is not assigned to the user.",
                    HttpStatus.NOT_FOUND));
        }
    }

    @Override
    @Transactional
    public Either<Status, UserDevice> assignDeviceToUser(String serialNumber, User user) {
        Device device = deviceRepository.findBySerialNumber(serialNumber).orElse(null);
        if (device == null) {
            return Either.left(new Status("Device with serial number " + serialNumber + " does not exist.",
                    HttpStatus.NOT_FOUND));
        }
        if (!userDeviceRepository.existsByUserAndSerialNumber(user, serialNumber)) {
            UserDevice userDevice = new UserDevice();
            userDevice.setUser(user);
            userDevice.setDevice(device);
            userDeviceRepository.save(userDevice);
            return Either.right(userDevice);
        } else {
            return Either.left(new Status("Device with serial number " + serialNumber + " is already assigned to the user.",
                    HttpStatus.CONFLICT));
        }
    }
}
