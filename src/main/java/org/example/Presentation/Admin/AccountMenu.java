package org.example.Presentation.Admin;

import org.example.Dao.AccountDao;
import org.example.Dao.EmployeeDao;
import org.example.Util.ColorText;
import org.example.Util.Notification;

import java.sql.SQLException;
import java.util.Scanner;

public class AccountMenu {
    /**
     * ******************ACCOUNT MANAGEMENT****************
     * 1. Danh sách tài khoản
     * 2. Tạo tài khoản mới
     * 3. Cập nhật trạng thái tài khoản
     * 4. Tìm kiếm tài khoản
     * 5. Thoát
     * Lưu ý:
     * - Khi tìm kiếm tài khoản cho phép tìm theo username hoặc tên nhân viên
     * và cho phép người dùng cập nhật trạng thái tài khoản
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            showAccountMenu(scanner);
        } catch (SQLException | ClassNotFoundException e) {
            e.getStackTrace();
        }
    }

    public static void showAccountMenu(Scanner scanner) throws SQLException, ClassNotFoundException {
        String choice;
        do {
            //
            System.out.println(ColorText.WHITE_BRIGHT + "◇⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤◇");
            System.out.println("⎜                 ACCOUNT MENU                   ⎜");
            System.out.println("◇⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤◇");
            System.out.println("⎜     ❖  1. Danh sách tài khoản                  ⎜");
            System.out.println("⎜     ❖  2. Tạo tài khoản mới                    ⎜");
            System.out.println("⎜     ❖  3. Cập nhật trạng thái tài khoản        ⎜");
            System.out.println("⎜     ❖  4. Tìm kiếm tài khoản                   ⎜");
            System.out.println("⎜     ❖  5. Thoát                                ⎜");
            System.out.println("◇⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤◇" + ColorText.RESET);
            System.out.println();
            System.out.print("Chọn lệnh : \n");
            choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    // 1. Danh sách tài khoản
                    AccountDao.displayAccounts(scanner);
                }
                case "2" -> {
                    // 2. Tạo tài khoản mới
                    AccountDao.createNewAcc(scanner);
                }
                case "3" -> {
                    // 3. Cập nhật trạng thái tài khoản
                    AccountDao.updateAccStatus(scanner);
                }
                case "4" -> {
                    // 4. Tìm kiếm tài khoản
                    AccountDao.searchAcc(scanner);
                }
                case "5" -> {
                    Notification.successMessage("Đã quay về kho");
                }
                default -> Notification.errorMessage("Lệnh nhập phải là số từ 1 - 5, xin hãy nhập lại");
            }
        } while (!choice.equals("5"));
    }
}
