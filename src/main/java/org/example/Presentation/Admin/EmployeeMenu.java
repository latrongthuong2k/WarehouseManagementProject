package org.example.Presentation.Admin;

import org.example.Dao.EmployeeDao;
import org.example.Dao.ProductDao;
import org.example.Util.ColorText;
import org.example.Util.Notification;

import java.sql.SQLException;
import java.util.Scanner;

public class EmployeeMenu {
    /* ******************EMPLOYEE MANAGEMENT****************
     * 1. Danh sách nhân viên
     * 2. Thêm mới nhân viên
     * 3. Cập nhật thông tin nhân viên
     * 4. Cập nhật trạng thái nhân viên
     * 5. Tìm kiếm nhân viên
     * 6. Thoát
     * Lưu ý:
     * - Khi hiển thị danh sách và tìm kiếm nhân viên mỗi lần hiển thị tối đa 10
     * nhân viên và được sắp xếp theo tên nhân viên tăng dần
     * - Tìm kiếm nhân viên theo mã hoặc theo tên nhân viên
     * - Khi cập nhật trạng thái nhân viên cho người dùng chọn trạng thái nhân
     * viên (Hoạt động-0 | Nghỉ chế độ-1 | Nghỉ việc-2).
     * - Trạng thái nhân viên khi cập nhật nghỉ chế độ, nghỉ việc thì tự động cập
     * nhật trạng thái Account thành Block
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            showEmpMenu(scanner);
        } catch (SQLException | ClassNotFoundException e) {
            e.getStackTrace();
        }
    }

    public static void showEmpMenu(Scanner scanner) throws SQLException, ClassNotFoundException {
        String choice;
        do {
            //
            System.out.println(ColorText.WHITE_BRIGHT + "◇⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤◇");
            System.out.println("⎜                 EMPLOYEE MENU                  ⎜");
            System.out.println("◇⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤◇");
            System.out.println("⎜     ❖  1. Danh sách nhân viên                  ⎜");
            System.out.println("⎜     ❖  2. Thêm mới nhân viên                   ⎜");
            System.out.println("⎜     ❖  3. Cập nhật thông tin nhân viên         ⎜");
            System.out.println("⎜     ❖  4. Cập nhật trạng thái nhân viên        ⎜");
            System.out.println("⎜     ❖  5. Tìm kiếm nhân viên                   ⎜");
            System.out.println("⎜     ❖  6. Thoát                                ⎜");
            System.out.println("◇⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤⏤◇" + ColorText.RESET);
            System.out.println();
            System.out.print("Chọn lệnh : \n");
            choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    // 1. Danh sách nhân viên
                    EmployeeDao.displayEmp(scanner);
                }
                case "2" -> {
                    // 2. Thêm mới nhân viên
                    EmployeeDao.addEmp(scanner);
                }
                case "3" -> {
                    // 3. Cập nhật thông tin nhân viên
                    EmployeeDao.updateEmpInfo(scanner);
                }
                case "4" -> {
                    // 4. Cập nhật trạng thái nhân viên
                    EmployeeDao.updateEmployeeStatus(scanner);
                }
                case "5" -> {
                    // 5. Tìm kiếm nhân viên
                    EmployeeDao.searchEmp(scanner);
                }
                case "6" -> {
                    Notification.successMessage("Đã quay về kho");
                }
                default -> Notification.errorMessage("Lệnh nhập phải là số từ 1 - 6, xin hãy nhập lại");
            }
        } while (!choice.equals("6"));
    }
}
