package org.example.Presentation.Admin;

import org.example.Controller.AdminController;
import org.example.Dao.BillDao;
import org.example.Dao.Bill_DetailDao;
import org.example.Util.ColorText;
import org.example.Util.Notification;

import java.sql.SQLException;
import java.util.Scanner;

public class ReceiptMenu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            showReceiptMenu(scanner);
        } catch (SQLException | ClassNotFoundException e) {
            e.getStackTrace();
        }
    }

    /**
     * ******************RECEIPT MANAGEMENT****************
     * 1. Danh sách phiếu nhập
     * 2. Tạo phiếu nhập
     * 3. Cập nhật thông tin phiếu nhập
     * 4. Chi tiết phiếu nhập
     * 5. Duyệt phiếu nhập
     * 6. Tìm kiếm phiếu nhập
     * 7. Thoát
     * Lưu ý:
     * - Khi cập nhật phiếu nhập, cho phép cập nhật cả chi tiết phiếu nhập của
     * phiếu nhập đó
     * - Chỉ được cập nhật thông tin phiếu nhập khi trạng thái đang ở trạng thái
     * Tạo hoặc hủy
     * - Khi duyệt phiếu nhập chuyển trạng thái từ tạo thành duyệt
     * - Khi tìm kiếm phiếu nhập, cho phép cập nhật và duyệt phiếu nhập
     * - Cập nhật phiếu nhập, tìm kiếm phiếu nhập, duyệt phiếu nhập theo mã
     * hoặc mã code phiếu nhâp
     * - Khi duyệt phiếu nhập cho phép cộng số lượng sản phẩm nhập vào số
     * lượng của sản phẩm
     */
    public static void showReceiptMenu(Scanner scanner) throws SQLException, ClassNotFoundException {
        String choice;
        do {
            System.out.println(ColorText.WHITE_BRIGHT + "◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇");
            System.out.println("⎜                 RECEIPT MENU                ⎜");
            System.out.println("⎜⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎜");
            System.out.println("⎜      ❖  1. Danh sách phiếu nhập             ⎜");
            System.out.println("⎜      ❖  2. Tạo phiếu nhập                   ⎜");
            System.out.println("⎜      ❖  3. Cập nhật thông tin phiếu nhập    ⎜");
            System.out.println("⎜      ❖  4. Chi tiết phiếu nhập              ⎜");
            System.out.println("⎜      ❖  5. Duyệt phiếu nhập                 ⎜");
            System.out.println("⎜      ❖  6. Tìm kiếm phiếu nhập              ⎜");
            System.out.println("⎜      ❖  7. Thoát                            ⎜");
            System.out.println("◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇" + ColorText.RESET);
            System.out.println();
            System.out.print("Chọn lệnh : \n");
            choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    // 1.  Danh sách phiếu nhập
                    BillDao.displayBill(false, null, null);
                }
                case "2" -> {
                    // 2. Tạo phiếu nhập
                    BillDao.createBill(scanner, false, AdminController.getEmpId());
                }
                case "3" -> {
                    // 3. Cập nhật thông tin phiếu nhập
                    BillDao.updateBill(scanner, false, null);
                }
                case "4" -> {
                    // 4. Chi tiết phiếu nhập
                    Bill_DetailDao.displayBillDetail(scanner, false, null);
                }
                case "5" -> {
                    // 5. Duyệt phiếu nhập
                    BillDao.approveBill(scanner, false, AdminController.getEmpId());
                }
                case "6" -> {
                    // 6. Tìm kiếm phiếu nhập
                    BillDao.searchBillForAdminAndUser(scanner, false, AdminController.getEmpId());
                }
                case "7" -> {
                    Notification.successMessage("Đã quay về kho");
                }
                default -> Notification.errorMessage("Lệnh nhập phải là số từ 1 - 6, xin hãy nhập lại");
            }
        } while (!choice.equals("7"));
    }
}
