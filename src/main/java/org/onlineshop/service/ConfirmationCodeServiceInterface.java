package org.onlineshop.service;

import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.User;

interface ConfirmationCodeServiceInterface {

    void confirmationCodeManager(User user);

    void sendCodeByEmail(String code, User user);

    void saveConfirmationCode(String generatedCode, User user);

    String generateCode();

    User changeConfirmationStatusByCode(String code);

    ConfirmationCode findCodeByUser(User user);
}
