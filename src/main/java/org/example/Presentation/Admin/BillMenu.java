package org.example.Presentation.Admin;

import org.example.Controller.AdminController;
import org.example.Dao.BillDao;
import org.example.Dao.Bill_DetailDao;
import org.example.Util.ColorText;
import org.example.Util.Notification;

import java.sql.SQLException;
import java.util.Scanner;

public class BillMenu {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        try {
//            showBillMenu(scanner);
//        } catch (SQLException | ClassNotFoundException e) {
//            e.getStackTrace();
//        }
//    }

    /**
     * ******************BILL MANAGEMENT****************
     * 1. Danh sách phiếu xuất
     * 2. Tạo phiếu xuất
     * 3. Cập nhật thông tin phiếu xuất
     * 4. Chi tiết phiếu xuất
     * 5. Duyệt phiếu xuất
     * 6. Tìm kiếm phiếu xuất
     * 7. Thoát
     * Lưu ý:
     * - Khi cập nhật phiếu xuất, cho phép cập nhật cả chi tiết phiếu xuất của
     * phiếu xuất đó
     * - Chỉ được cập nhật thông tin phiếu xuất khi trạng thái đang ở trạng thái
     * Tạo hoặc hủy
     * - Khi duyệt phiếu xuất chuyển trạng thái từ tạo thành duyệt
     * - Khi tìm kiếm phiếu xuất, cho phép cập nhật và duyệt phiếu xuất
     * - Cập nhật phiếu xuất, tìm kiếm phiếu xuất, duyệt phiếu xuất theo mã hoặc
     * mã code phiếu xuất
     * - Khi duyệt phiếu xuất cho phép trừ số lượng sản phẩm xuất vào số lượng
     * của sản phẩm
     */
    public static void showBillMenu(Scanner scanner) throws SQLException, ClassNotFoundException {
        String choice;
        do {
            System.out.println(ColorText.WHITE_BRIGHT + "◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇");
            System.out.println("⎜                 BILL MENU                   ⎜");
            System.out.println("⎜⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎜");
            System.out.println("⎜      ❖  1. Danh sách phiếu xuất             ⎜");
            System.out.println("⎜      ❖  2. Tạo phiếu xuất                   ⎜");
            System.out.println("⎜      ❖  3. Cập nhật thông tin phiếu xuất    ⎜");
            System.out.println("⎜      ❖  4. Chi tiết phiếu xuất              ⎜");
            System.out.println("⎜      ❖  5. Duyệt phiếu xuất                 ⎜");
            System.out.println("⎜      ❖  6. Tìm kiếm phiếu xuất              ⎜");
            System.out.println("⎜      ❖  7. Thoát                            ⎜");
            System.out.println("◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇" + ColorText.RESET);
            System.out.println();
            System.out.print("Chọn lệnh : \n");
            choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    // 1.  Danh sách phiếu xuất
                    BillDao.displayBill(true, null, null);
                }
                case "2" -> {
                    // 2. Tạo phiếu xuất
                    BillDao.createBill(scanner, true, AdminController.getEmpId());
                }
                case "3" -> {
                    // 3. Cập nhật thông tin phiếu xuất
                    BillDao.updateBill(scanner, true, null);
                }
                case "4" -> {
                    // 4. Chi tiết phiếu xuất
                    Bill_DetailDao.displayBillDetail(scanner, true, null);
                }
                case "5" -> {
                    // 5. Duyệt phiếu xuất
                    BillDao.approveBill(scanner, true, AdminController.getEmpId());
                }
                case "6" -> {
                    // 6. Tìm kiếm phiếu xuất
                    BillDao.searchBillForAdminAndUser(scanner, true, AdminController.getEmpId());
                }
                case "7" -> {
                    Notification.successMessage("Đã quay về kho");
                }
                default -> Notification.errorMessage("Lệnh nhập phải là số từ 1 - 6, xin hãy nhập lại");
            }
        } while (!choice.equals("7"));
    }
}
