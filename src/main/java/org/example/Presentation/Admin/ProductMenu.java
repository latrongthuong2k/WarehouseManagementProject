package org.example.Presentation.Admin;

import org.example.Dao.ProductDao;
import org.example.Util.ColorText;
import org.example.Util.Notification;

import java.sql.SQLException;
import java.util.Scanner;

public class ProductMenu {
    /*
     * ******************PRODUCT MANAGEMENT****************
     * 1. Danh sách sản phẩm
     * 2. Thêm mới sản phẩm
     * 3. Cập nhật sản phẩm
     * 4. Tìm kiếm sản phẩm
     * 5. Cập nhật trạng thái sản phẩm
     * 6. Thoát
     * Lưu ý:
     * - Khi hiển thị danh sách và tìm kiếm sản phẩm mỗi lần tối đa 10 sản phẩm
     * - Tìm kiếm sản phẩm theo tên sản phẩm
     * - Khi cập nhật trạng thái sản phẩm, cho người dùng chọn trạng thái sản
     * phẩm cần cập nhật (Hoạt động – true | Không hoạt động - false)
     * - Khi thêm mới, cập nhật sản phẩm không thêm hoặc sửa số lượng sản
     * phẩm
     */
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        try {
//            showProductMenu(scanner);
//        } catch (SQLException | ClassNotFoundException e) {
//            e.getStackTrace();
//        }
//    }

    public static void showProductMenu(Scanner scanner) throws SQLException, ClassNotFoundException {
        String choice;
        do {
            System.out.println(ColorText.WHITE_BRIGHT + "◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇");
            System.out.println("⎜                 PRODUCT MENU                ⎜");
            System.out.println("⎜⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎜");
            System.out.println("⎜      ❖  1. Danh sách sản phẩm               ⎜");
            System.out.println("⎜      ❖  2. Thêm mới sản phẩm                ⎜");
            System.out.println("⎜      ❖  3. Cập nhật sản phẩm                ⎜");
            System.out.println("⎜      ❖  4. Tìm kiếm sản phẩm                ⎜");
            System.out.println("⎜      ❖  5. Cập nhật trạng thái sản phẩm     ⎜");
            System.out.println("⎜      ❖  6. Thoát                            ⎜");
            System.out.println("◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇" + ColorText.RESET);
            System.out.println();
            System.out.print("Chọn lệnh : \n");
            choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    // 1. Danh sách sản phẩm
                    ProductDao.displayProducts(scanner);
                }
                case "2" -> {
                    // 2. Thêm mới sản phẩm
                    ProductDao.addProduct(scanner);
                }
                case "3" -> {
                    // 3. Cập nhật sản phẩm
                    ProductDao.updateProduct(scanner);
                }
                case "4" -> {
                    // 4. Tìm kiếm sản phẩm
                    ProductDao.searchProduct(scanner);
                }
                case "5" -> {
                    // 5. Cập nhật trạng thái sản phẩm
                    ProductDao.updateProductStatus(scanner);
                }
                case "6" -> {
                    Notification.successMessage("Đã quay về kho");
                }
                default -> Notification.errorMessage("Lệnh nhập phải là số từ 1 - 6, xin hãy nhập lại");
            }
        } while (!choice.equals("6"));
    }
}
