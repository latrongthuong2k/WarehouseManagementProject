package org.example.Model;

import org.example.Dao.EmployeeDao;
import org.example.Util.ColorText;
import org.example.Util.ConfigInput;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.SQLException;
import java.util.Scanner;

import static org.example.Util.Validate.*;

public class Employee {
    private String empId;
    private String empName;
    private String birthday;
    private String email;
    private String phoneNumber;
    private String address;
    private Integer status;

    public Employee() {
    }

    public Employee(String empId, String empName, String birthday, String email, String phoneNumber, String address, Integer status) {
        this.empId = empId;
        this.empName = empName;
        this.birthday = birthday;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.status = status;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    // Input
    public void inputData(Scanner scanner, String nameAction) throws SQLException, ClassNotFoundException {
        String input;
        // Nhập mã nhân viên
        if (this.empId == null) {
            Notification.inputPrint("Nhập mã nhân viên: ");
            do {
                input = scanner.nextLine().trim();
                if (!isInputValid(input)) continue;
                else if (input.length() > 5) {
                    Notification.errorMessage("Input chỉ được tối đa 5 ký tự cho phép, Hãy thử lại!");
                    continue;
                }
                if (EmployeeDao.checkExistEmp(input, null)) {
                    Notification.errorMessage("ID nhân viên đã tồn tại, vui lòng nhập lại ID khác");
                    continue;
                }
                break;
            } while (true); // nếu là null và empty và giới hạn char
            setEmpId(input);
            Notification.successMessage("✓ SUCCESS!");
        }
        // Nhập tên nhân viên
        if (this.empName == null) {
            Notification.inputPrint("Nhập tên nhân viên : ");
            do {
                input = scanner.nextLine().trim();
                if (!isInputValid(input)) continue;
                else if (input.length() > 100) {
                    Notification.errorMessage("Input chỉ được tối đa 100 ký tự cho phép, Hãy thử lại!");
                    continue;
                }
                if (EmployeeDao.checkExistEmp(null, input)) {
                    Notification.errorMessage("Tên nhân viên đã tồn tại, vui lòng nhập lại tên khác");
                    continue;
                }
                break;
            } while (true); // nếu là null và empty
            setEmpName(input);
            Notification.successMessage("✓ SUCCESS!");

        } else {
            Notification.inputPrint("Nhập tên nhân viên : ");
            do {
                input = scanner.nextLine().trim();
                if (!isInputValid(input)) continue;
                else if (input.length() > 100) {
                    Notification.errorMessage("Input chỉ được tối đa 100 ký tự cho phép, Hãy thử lại!");
                    continue;
                }
                // so sánh khác chính nó
                if (!this.empName.equals(input)) {
                    if (EmployeeDao.checkExistEmp(null, input)) {
                        Notification.errorMessage("Tên nhân viên đã tồn tại, vui lòng nhập lại tên khác");
                        continue;
                    }
                }
                break;
            } while (true); // nếu là null và empty
            setEmpName(input);
            Notification.successMessage("✓ SUCCESS!");
        }

        // Nhập ngày sinh

        Notification.inputPrint("Nhập ngày sinh yyyy-MM-dd : ");
        do {
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            if (!Validate.isValidDate(input)) continue;
            break;
        } while (true);
        setBirthday(input);
        Notification.successMessage("✓ SUCCESS!");

        // Nhập enail
        do {
            Notification.inputPrint("Nhập email : ");
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            else if (input.length() > 100) {
                Notification.errorMessage("Input chỉ được tối đa 100 ký tự cho phép, Hãy thử lại!");
                continue;
            }
            if (!Validate.isEmailValid(input)) continue;
            break;
        } while (true);
        setEmail(input);
        Notification.successMessage("✓ SUCCESS!");


        // Nhập số điện thoại
        Notification.inputPrint("Nhập số điện thoại : ");
        do {
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            if (!isPhoneNumberValid(input)) continue;
            break;
        } while (true);
        setPhoneNumber(input);
        Notification.successMessage("✓ SUCCESS!");


        // Nhập địa chỉ
        Notification.inputPrint("Nhập địa chỉ : ");
        do {
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            break;
        } while (true);
        setAddress(input);
        Notification.successMessage("✓ SUCCESS!");


        // Nhập trạng thái
        if (status == null) {
            int intStatus;
            Notification.inputPrint("Nhập trạng thái : ( 1 - Hoạt động | 2 - Nghỉ chế độ | 3 - Nghỉ việc ) : ");
            do {
                input = scanner.nextLine().trim();
                if (!isInputValid(input)) continue;
                switch (input) {
                    case "1" -> intStatus = 0;
                    case "2" -> intStatus = 1;
                    case "3" -> intStatus = 2;
                    default -> {
                        Notification.errorMessage("Giá trị nhập phải là số : 1, 2 hoặc 3, Hãy thử lại!");
                        continue;
                    }
                }
                break;
            } while (true);
            setStatus(intStatus);
            Notification.successMessage("✓ SUCCESS!");
        }
        // display
        displayInfo(nameAction);
        System.out.println();
    }

    public void displayInfo(String nameAction) {
        String statusTextDisplay = null;
        if (status == 1)
            statusTextDisplay = "Nghỉ chế độ";
        else if (status == 0)
            statusTextDisplay = "Hoạt động";
        else if (status == 2)
            statusTextDisplay = "Nghỉ việc";

        System.out.println("Thông tin hiện của nhân viên vừa thực hiện " + nameAction + " là: ");
        System.out.println(ColorText.WHITE_BRIGHT +
                "\n EmpID : " + empId +
                "\n EmpName : " + empName +
                "\n Birthday : " + birthday +
                "\n Email : " + email +
                "\n Phone : " + phoneNumber +
                "\n Address : " + address +
                "\n Status : " + statusTextDisplay + ColorText.RESET);
    }
}
