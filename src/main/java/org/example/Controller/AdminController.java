package org.example.Controller;

import org.example.Presentation.Admin.*;
import org.example.Util.ColorText;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.SQLException;
import java.util.Scanner;

public class AdminController {
    public static String getEmpId() {
        return empId;
    }

    public static void setEmpId(String empId) {
        AdminController.empId = empId;
    }

    private static String empId;

    public static void showWarehouse() throws SQLException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String choice;
        do {
            System.out.println(ColorText.WHITE_BRIGHT + "◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇");
            System.out.println("⎜                  WAREHOUSE MANAGEMENT (ADMIN)                   ⎜");
            System.out.println("⎜⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎜");
            System.out.println("⎜                   ❖  1. Quản lý sản phẩm                        ⎜");
            System.out.println("⎜                   ❖  2. Quản lý nhân viên                       ⎜");
            System.out.println("⎜                   ❖  3. Quản lý tài khoản                       ⎜");
            System.out.println("⎜                   ❖  4. Quản lý phiếu nhập                      ⎜");
            System.out.println("⎜                   ❖  5. Quản lý phiếu xuất                      ⎜");
            System.out.println("⎜                   ❖  6. Quản lý báo cáo                         ⎜");
            System.out.println("⎜                   ❖  7. Đăng xuất                               ⎜");
            System.out.println("◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇" + ColorText.RESET);
            System.out.println();
            System.out.print("Chọn lệnh : \n");
            choice = scanner.nextLine().trim();
            if (!Validate.isInputValid(choice)) continue;
            switch (choice) {
                case "1" -> {
                    //1. Quản lý sản phẩm
                    ProductMenu.showProductMenu(scanner);
                }
                case "2" -> {
                    //2. Quản lý nhân viên
                    EmployeeMenu.showEmpMenu(scanner);
                }
                case "3" -> {
                    //3. Quản lý tài khoản
                    AccountMenu.showAccountMenu(scanner);
                }
                case "4" -> {
                    //4. Quản lý phiếu nhập
                    ReceiptMenu.showReceiptMenu(scanner);
                }
                case "5" -> {
                    //5. Quản lý phiếu xuất
                    BillMenu.showBillMenu(scanner);
                }
                case "6" -> {
                    //6. Quản lý báo cáo
                    ReportMenu.showReportMenu(scanner);
                }
                case "7" -> {

                    //7. đăng xuất quay lại màng hình đăng nhập
                }
                default -> Notification.errorMessage("Lệnh không đúng, xin hãy nhập lại");
            }
        } while (!choice.equals("7"));
    }
}
