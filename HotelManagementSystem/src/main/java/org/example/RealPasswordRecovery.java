package org.example;

import java.util.Scanner;

class RealPasswordRecovery implements PasswordRecoveryService {
    @Override
    public void recoverPassword(String adminEmail) {
        System.out.println("Password recovery process initiated for: " + adminEmail);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter new password :");
        String newPassword = scanner.nextLine();

        Manager.getmanager().setPassword(newPassword);

    }
}

