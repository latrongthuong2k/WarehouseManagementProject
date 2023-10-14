package org.example.Controller;

import org.example.Dao.BillDao;
import org.example.Util.ColorText;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.SQLException;
import java.util.Scanner;

public class UserController {
    public static String getEmpId() {
        return empId;
    }

    public static void setEmpId(String empId) {
        UserController.empId = empId;
    }

    private static String empId;

    // warehouse menu
    public static void showWarehouse() throws SQLException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String choice;
        do {
            System.out.println(ColorText.WHITE_BRIGHT + "◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇");
            System.out.println("⎜                  WAREHOUSE MANAGEMENT (USER)                    ⎜");
            System.out.println("⎜⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎜");
            System.out.println("⎜              ❖  1. Danh sách phiếu nhập theo trạng thái         ⎜");
            System.out.println("⎜              ❖  2. Tạo phiếu nhập                               ⎜");
            System.out.println("⎜              ❖  3. Cập nhật phiếu nhập                          ⎜");
            System.out.println("⎜              ❖  4. Tìm kiếm phiếu nhập                          ⎜");
            System.out.println("⎜              ❖  5. Danh sách phiếu xuất theo trạng thái         ⎜");
            System.out.println("⎜              ❖  6. Tạo phiếu xuất                               ⎜");
            System.out.println("⎜              ❖  7. Cập nhật phiếu xuất                          ⎜");
            System.out.println("⎜              ❖  8. Tìm kiếm phiếu xuất                          ⎜");
            System.out.println("⎜              ❖  9. Đăng xuất                                    ⎜");
            System.out.println("◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇" + ColorText.RESET);
            System.out.println();
            System.out.print("Chọn lệnh : \n");
            choice = scanner.nextLine().trim();
            if (!Validate.isInputValid(choice)) continue;
            switch (choice) {
                case "1" -> {
                    //1. Danh sách phiếu nhập theo trạng thái
                    chooseOptionStatus(scanner, false);
                }
                case "2" -> {
                    //2. Tạo phiếu nhập
                    BillDao.createBill(scanner, false, empId);
                }
                case "3" -> {
                    //3. Cập nhật phiếu nhập
                    BillDao.updateBill(scanner, false, empId);
                }
                case "4" -> {
                    //4. Tìm kiếm phiếu nhập
                    BillDao.searchBillForAdminAndUser(scanner, false, null);
                }
                case "5" -> {
                    //5. Danh sách phiếu xuất theo trạng thái
                    chooseOptionStatus(scanner, true);
                }
                case "6" -> {
                    //6. Tạo phiếu xuất
                    BillDao.createBill(scanner, true, empId);
                }
                case "7" -> {
                    //6. Cập nhật phiếu xuất
                    BillDao.updateBill(scanner, true, empId);
                }
                case "8" -> {
                    //6. Tìm kiếm phiếu xuất
                    BillDao.searchBillForAdminAndUser(scanner, true, null);
                }
                case "9" -> {
                    //9. đăng xuất quay lại màng hình đăng nhập
                }
                default -> Notification.errorMessage("Lệnh không đúng, xin hãy nhập lại");
            }
        } while (!choice.equals("9"));
    }

    private static void chooseOptionStatus(Scanner scanner, boolean billType) throws SQLException, ClassNotFoundException {
        String choiceOption;
        String nameType = BillDao.getTypeName(billType); // nhập hoặc xuất
        do {
            String text = "Nhập (1) để hiển thị bản phiếu " + nameType + " đã (Tạo)\n" +
                    "Nhập (2) để hiển thị bản phiếu " + nameType + " đã (Huỷ)\n" +
                    "Nhập (3) để hiển thị bản phiếu " + nameType + " đã (Duyệt)\n" +
                    "Nhập (4 hoặc exit) để để quay lại menu \n";
            System.out.println(ColorText.CYAN_BRIGHT + text + ColorText.RESET);
            choiceOption = scanner.nextLine().trim();
            if (!Validate.isInputValid(choiceOption)) continue;
            if (choiceOption.equalsIgnoreCase("exit")
                    || choiceOption.equals("4")) break;
            switch (choiceOption) {
                case "1" -> BillDao.displayBill(billType, empId, "0");
                case "2" -> BillDao.displayBill(billType, empId, "1");
                case "3" -> BillDao.displayBill(billType, empId, "2");
                default -> {
                    Notification.waringMessage("Số nhập không hợp lệ, " +
                            "hãy chắc chắn bạn nhập các số (1 , 2 hoặc 3). Hãy thử lại!");
//                                continue;
                }
            }
        } while (true);
    }

}
