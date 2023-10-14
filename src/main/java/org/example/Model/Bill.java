package org.example.Model;

import org.example.Dao.BillDao;
import org.example.Dao.EmployeeDao;
import org.example.Util.Notification;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

import static org.example.Util.Validate.isInputValid;

public class Bill {
    private Long billId;
    private String billCode;
    private Boolean billType;
    private String empIdCreated;
    private LocalDate created;
    private String empIdAuth;
    private LocalDate authDate;
    private Integer billStatus;

    public Bill() {
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public Boolean getBillType() {
        return billType;
    }

    public void setBillType(Boolean billType) {
        this.billType = billType;
    }

    public String getEmpIdCreated() {
        return empIdCreated;
    }

    public void setEmpIdCreated(String empIdCreated) {
        this.empIdCreated = empIdCreated;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public String getEmpIdAuth() {
        return empIdAuth;
    }

    public void setEmpIdAuth(String empIdAuth) {
        this.empIdAuth = empIdAuth;
    }

    public LocalDate getAuthDate() {
        return authDate;
    }

    public void setAuthDate(LocalDate authDate) {
        this.authDate = authDate;
    }

    public Integer getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(Integer billStatus) {
        this.billStatus = billStatus;
    }

    // Phương thức phân tích ngày từ chuỗi

    public void inputData(Scanner scanner, boolean permission) throws SQLException, ClassNotFoundException {
        String input;

        // Nhập mã hóa đơn
        if (billCode == null) {
            do {
                Notification.inputPrint("Nhập mã hóa đơn (Bill Code): ");
                input = scanner.nextLine().trim();
                if (!isInputValid(input)) continue;
                if (input.length() > 10) {
                    Notification.errorMessage("Input chỉ được tối đa 10 ký tự cho phép, Hãy thử lại!");
                    continue;
                }
                if (BillDao.checkExistsBill(null, input, billType)) {
                    Notification.errorMessage("Bill code đã tồn tại ở trong bảng phiếu " + BillDao.getTypeName(billType) +
                            ", hãy thử lại mã khác!");
                    continue;
                }
                if (BillDao.checkExistsBill(null, input, !billType)) {
                    Notification.errorMessage("Bill code đã tồn tại ở trong bảng phiếu " + BillDao.getTypeName(!billType) +
                            ", hãy thử lại mã khác!");
                    continue;
                }
                break;
            } while (true);
            setBillCode(input);
            Notification.successMessage("✓ SUCCESS!");
        }

        // Nhập loại hóa đơn
//        Boolean status = null;
//        do {
//            Notification.inputPrint("Nhập loại hóa đơn (1 cho PHIẾU NHẬP, 0 cho PHIẾU XUẤT): ");
//            input = scanner.nextLine().trim();
//            if (!isInputValid(input)) continue;
//            else if (input.length() > 10) {
//                Notification.errorMessage("Input chỉ được tối đa 10 ký tự cho phép, Hãy thử lại!");
//                continue;
//            }
//            switch (input) {
//                case "1" -> status = true;
//                case "0" -> status = false;
//                default -> Notification.errorMessage("Lệnh nhập không phải là số 1 hoặc 0, hãy nhập lại");
//            }
//            break;
//
//        } while (true);
//        setBillType(status);
//        Notification.successMessage("✓ SUCCESS!");

        // Mã nhân viên
        if (empIdCreated == null) {
            do {
                Notification.inputPrint("Nhập mã nhân viên : ");
                input = scanner.nextLine().trim();
                if (!isInputValid(input)) continue;
                else if (input.length() > 5) {
                    Notification.errorMessage("Input chỉ được tối đa 5 ký tự cho phép, Hãy thử lại!");
                    continue;
                }
                if (!EmployeeDao.checkExistEmp(input, null)) {
                    Notification.errorMessage("Không tồn tại nhân viên với ID : " + input + " hãy thử lại!");
                    continue;
                }
                break;
            } while (true);
            setEmpIdCreated(input);
            Notification.successMessage("✓ SUCCESS!");
        }

        if (billStatus == null) {
            int numberOfStatus;
            if (!permission) {
                do {
                    Notification.inputPrint("Nhập số để đặt trạng thái cho bill\n" +
                            " 0 (Tạo) hoặc 1 (Huỷ) hoặc 2 (Duyệt)  : ");
                    input = scanner.nextLine().trim();
                    if (!isInputValid(input)) continue;
                    else if (input.length() > 10) {
                        Notification.errorMessage("Input chỉ được tối đa 10 ký tự cho phép, Hãy thử lại!");
                        continue;
                    }
                    switch (input) {
                        case "0" -> numberOfStatus = 0;
                        case "1" -> numberOfStatus = 1;
                        case "2" -> numberOfStatus = 2;
                        default -> {
                            Notification.errorMessage("Giá trị nhập phải là số : 0 (Tạo) hoặc 1 (Huỷ) hoặc 2 (Duyệt) ");
                            continue;
                        }
                    }
                    break;
                } while (true);
            } else {
                do {
                    Notification.inputPrint("Nhập số để đặt trạng thái cho bill\n" +
                            " 0 (Tạo) hoặc 1 (Huỷ): ");
                    input = scanner.nextLine().trim();
                    if (!isInputValid(input)) continue;
                    else if (input.length() > 10) {
                        Notification.errorMessage("Input chỉ được tối đa 10 ký tự cho phép, Hãy thử lại!");
                        continue;
                    }
                    switch (input) {
                        case "0" -> numberOfStatus = 0;
                        case "1" -> numberOfStatus = 1;
                        default -> {
                            Notification.errorMessage("Giá trị nhập phải là số : 0 (Tạo) hoặc 1 (Huỷ) hoặc 2 (Duyệt) ");
                            continue;
                        }
                    }
                    break;
                } while (true);
            }
            setBillStatus(numberOfStatus);
            Notification.successMessage("✓ SUCCESS!");
        }

    }

}
