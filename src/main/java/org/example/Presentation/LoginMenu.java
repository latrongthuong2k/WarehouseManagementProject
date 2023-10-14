package org.example.Presentation;

import org.example.Dao.AccountDao;
import org.example.Dto.LoginResultDto;
import org.example.Util.ColorText;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.SQLException;
import java.util.Scanner;

public class LoginMenu {

    // admin account

    public static LoginResultDto getLoginMenu(Scanner scanner) {

        System.out.println("======== WELCOME ========");
        boolean isValid = false;
        boolean accountPermission = false; // tạm set mặt định là user
        String empId = null;
        String userName;
        String passwordInput;
        while (!isValid) {
            // userName
            System.out.print(ColorText.CYAN_BRIGHT + "Username : ");
            userName = scanner.nextLine().trim();
            if (!Validate.isInputValid(userName)) continue; // no null no empty
            System.out.print("\n");
            // pass
            System.out.print("Password : " + ColorText.RESET);
            passwordInput = scanner.nextLine().trim();
            if (!Validate.isInputValid(userName)) continue; // no null no empty
            System.out.print("\n");
            // check
            try {
                if (AccountDao.checkValidAccount(userName, passwordInput)) {
                    isValid = true;
                    // get account Permission
                    accountPermission = AccountDao.checkAccountPermission(userName);
                    // get empId
                    empId = AccountDao.getEmpId(userName);
                    System.out.println(); // cho khoảng cách ra
                } else
                    Notification.waringMessage("Tài khoảng hoặc mật khẩu chưa đúng, vui lòng thử lại.");
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return new LoginResultDto(empId, accountPermission);
    }

//    private static void registerMenu(Scanner scanner) {
//        boolean isValid = false;
//        String userName;
//        String passwordInput;
//        while (!isValid) {
//            try (Connection connection = getConnection()) {
//                AccountDao accountDao = new AccountDao(connection);
//                // userName
//                Notification.inputPrint("Nhập tên người dùng ( tối đa là 30 ký tự ): ");
//                // loop user name
//                while (true) {
//                    userName = scanner.nextLine().trim();
//                    if (userName.length() > 30) {
//                        Notification.errorMessage("Tên người dùng phải tối thiểu 30 ký tự");
//                    }
//                    if (accountDao.checkExistsUser(userName))
//                        Notification.errorMessage("Tên người dùng đã tồn tại, xin hãy nhập tên khác");
//                    else
//                        break;
//                }
//                // pass
//                Notification.inputPrint("Mật khẩu ( tối thiểu 6 ký tự và tối đa là 30 ký tự ): ");
//                // loop pass
//                while (true) {
//                    passwordInput = scanner.nextLine().trim();
//                    if (passwordInput.length() > 30) {
//                        Notification.errorMessage("mật khẩu bị vượt quá 30 ký tự");
//                    } else if (passwordInput.length() < 6) {
//                        Notification.errorMessage("mật khẩu không được ít hơn 6 ký tự");
//                    }else
//                        break;
//                }
//                isValid = true;
//            } catch (SQLException | ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//        }
}
