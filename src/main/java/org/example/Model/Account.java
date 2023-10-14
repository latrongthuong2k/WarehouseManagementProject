package org.example.Model;

import org.example.Dao.AccountDao;
import org.example.Dao.EmployeeDao;
import org.example.Util.ColorText;
import org.example.Util.Notification;

import java.sql.SQLException;
import java.util.Scanner;

import static org.example.Util.Validate.isInputValid;

public class Account {
    private Long accId;
    private String userName;
    private String password;
    private boolean permission;
    private String empId;
    private Boolean accStatus;

    public Account() {
    }

    public Long getAccId() {
        return accId;
    }

    public void setAccId(Long accId) {
        this.accId = accId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public Boolean isAccStatus() {
        return accStatus;
    }

    public void setAccStatus(Boolean accStatus) {
        this.accStatus = accStatus;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }


    public void inputData(Scanner scanner, String nameAction) throws SQLException, ClassNotFoundException {
        String input;
        // Nhập tên người dùng
        Notification.inputPrint("Nhập tên người dùng : ");
        do {
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            else if (input.length() > 30) {
                Notification.errorMessage("Input chỉ được tối đa 30 ký tự cho phép. Hãy thử lại!");
                continue;
            }
            if (AccountDao.checkExistsUser(input)) {
                Notification.errorMessage("Tên user đã tồn tại");
                continue;
            }
            break;
        } while (true);
        setUserName(input);
        Notification.successMessage("✓ SUCCESS!");

        // Nhập mật khẩu
        Notification.inputPrint("Nhập mật khẩu : ");
        do {
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            else if (input.length() > 30) {
                Notification.errorMessage("Input chỉ được tối đa 30 ký tự cho phép, Hãy thử lại!");
                continue;
            }
            if (!isPasswordValid(input)) {
                Notification.errorMessage("Mật khẩu phải có ít nhất 6 ký tự. Hãy thử lại.");
                continue;
            }
            break;
        } while (true);
        setPassword(input);
        Notification.successMessage("✓ SUCCESS!");

        // Nhập permission
        boolean permission;
        Notification.inputPrint("Nhập quyền cho tài khoảng ( 0 - admin, 1 - user ) : ");
        do {
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            switch (input) {
                case "0" -> permission = false;
                case "1" -> permission = true;
                default -> {
                    Notification.errorMessage("Giá trị nhập phải là 0 hoặc 1. Hãy thử lại!");
                    continue;
                }
            }
            break;
        } while (true);
        setPermission(permission);
        Notification.successMessage("✓ SUCCESS!");

        // Nhập Emp_id
        Notification.inputPrint("Nhập mã nhân viên tham chiếu tương ứng trong bảng sau : ");
        Notification.successMessage("-> VIEW MODE <-");
        EmployeeDao.displayEmp(scanner);
        Notification.successMessage("-> CONTINUE CREATE MODE <-");
        Notification.inputPrint("Nhập mã nhân viên tham chiếu : ");

        do {
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            if (!EmployeeDao.checkExistEmp(input, null)) {
                Notification.errorMessage("Không tồn tại nhân viên với ID : " + input + ". Hãy thử lại!");
                continue;
            } else if (!AccountDao.isEmpIdUnique(empId)) {
                Notification.errorMessage("Mã nhân viên đã được sử dụng, hãy thử lại ID khác");
            }
            break;
        } while (true);
        setEmpId(input);
        Notification.successMessage("✓ SUCCESS!");

        // Nhập acc status
        if (accStatus == null) {
            boolean subStatus;
            if (nameAction.equalsIgnoreCase("thêm mới"))
                Notification.inputPrint("Nhập trạng thái cho tài khoảng ( 0 - Block, 1 - Active ) : ");
            do {
                input = scanner.nextLine().trim();
                if (!isInputValid(input)) continue;
                switch (input) {
                    case "0" -> subStatus = false;
                    case "1" -> subStatus = true;
                    default -> {
                        Notification.errorMessage("Giá trị nhập phải là 0 hoặc 1, Hãy thử lại!");
                        continue;
                    }
                }
                break;
            } while (true);
            setAccStatus(subStatus);
            Notification.successMessage("✓ SUCCESS!");
        }

        // show info
        displayInfo(nameAction);
        System.out.println();
    }

    public void displayInfo(String nameAction) {
        String permissionDetail;
        if (!permission)
            permissionDetail = "Admin";
        else
            permissionDetail = "User";
        String passcode = "*";
        System.out.println("Thông tin acc vừa thực hiện " + nameAction + " là: ");
        System.out.println(ColorText.WHITE_BRIGHT +
                "\n User name : " + userName +
                "\n Password : " + passcode.repeat(password.length()) +
                "\n Role : " + permissionDetail +
                "\n Emp Id : " + empId +
                "\n Status : " + (accStatus ? "Active" : "Block") + ColorText.RESET);
    }
}
