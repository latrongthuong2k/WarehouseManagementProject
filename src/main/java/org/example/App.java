package org.example;

import org.example.Controller.AdminController;

import org.example.Controller.UserController;
import org.example.Dao.AccountDao;
import org.example.Dto.LoginResultDto;
import org.example.Presentation.LoginMenu;
import org.example.Util.Notification;


import java.sql.SQLException;
import java.util.Scanner;

public class App {

    public static LoginResultDto loginResultDto;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        do {
            loginResultDto = LoginMenu.getLoginMenu(scanner);
            if (!loginResultDto.accountPermission()) {
                // go to admin controller
                AdminController.setEmpId(loginResultDto.empId());
                try {
                    if (!AccountDao.isValidStatusAcc(loginResultDto.empId())) {
                        Notification.waringMessage("Tài khoảng đang bị block. Hãy thử lại!");
                        continue;
                    }
                    Notification.successMessage("✓ LOGIN SUCCESS!");
                    AdminController.showWarehouse();
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // go to user controller
                UserController.setEmpId(loginResultDto.empId());
                try {
                    if (!AccountDao.isValidStatusAcc(loginResultDto.empId())) {
                        Notification.waringMessage("Tài khoảng đang bị block. Hãy thử lại!");
                        continue;
                    }
                    Notification.successMessage("✓ LOGIN SUCCESS!");
                    UserController.showWarehouse();
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } while (true);
    }
}
