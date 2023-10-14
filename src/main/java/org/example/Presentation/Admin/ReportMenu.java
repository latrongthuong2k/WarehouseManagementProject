package org.example.Presentation.Admin;

import org.example.Dao.ReportDao;
import org.example.Util.ColorText;
import org.example.Util.Notification;

import java.sql.SQLException;
import java.util.Scanner;

public class ReportMenu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            showReportMenu(scanner);
        } catch (SQLException | ClassNotFoundException e) {
            e.getStackTrace();
        }
    }

    /**
     * ******************REPORT MANAGEMENT****************
     * 1. Thống kê chi phí theo ngày, tháng, năm
     * 2. Thống kê chi phí theo khoảng thời gian
     * 3. Thống kê doanh thu theo ngày, tháng, năm
     * 4. Thống kê doanh thu theo khoảng thời gian
     * 5. Thống kê số nhân viên theo từng trạng thái
     * 6. Thống kê sản phẩm nhập nhiều nhất trong khoảng thời gian
     * 7. Thống kê sản phẩm nhập ít nhất trong khoảng thời gian
     * 8. Thống kê sản phẩm xuất nhiều nhất trong khoảng thời gian
     * 9. Thống kê sản phẩm xuất ít nhất trong khoảng thời gian
     * 10. Thoát
     */
    public static void showReportMenu(Scanner scanner) throws SQLException, ClassNotFoundException {
        String choice;
        do {
            System.out.println(ColorText.WHITE_BRIGHT + "◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇");
            System.out.println("⎜                               REPORT MENU                                 ⎜");
            System.out.println("⎜⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎜");
            System.out.println("⎜      ❖  1. Thống kê chi phí theo ngày, tháng, năm                         ⎜");
            System.out.println("⎜      ❖  2. Thống kê chi phí theo khoảng thời gian                         ⎜");
            System.out.println("⎜      ❖  3. Thống kê doanh thu theo ngày, tháng, năm                       ⎜");
            System.out.println("⎜      ❖  4. Thống kê doanh thu theo khoảng thời gian                       ⎜");
            System.out.println("⎜      ❖  5. Thống kê số nhân viên theo từng trạng thái                     ⎜");
            System.out.println("⎜      ❖  6. Thống kê sản phẩm nhập nhiều nhất trong khoảng thời gian       ⎜");
            System.out.println("⎜      ❖  7. Thống kê sản phẩm nhập ít nhất trong khoảng thời gian          ⎜");
            System.out.println("⎜      ❖  8. Thống kê sản phẩm xuất nhiều nhất trong khoảng thời gian       ⎜");
            System.out.println("⎜      ❖  9. Thống kê sản phẩm xuất ít nhất trong khoảng thời gian          ⎜");
            System.out.println("⎜      ❖  10. Thoát                                                         ⎜");
            System.out.println("◇⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯◇" + ColorText.RESET);

            System.out.print("Chọn lệnh : \n");
            choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    // 1. Thống kê chi phí theo ngày, tháng, năm
                    ReportDao.callCostStatisticsByDMY(scanner);
                }
                case "2" -> {
                    // 2. Thống kê chi phí theo khoảng thời gian
                    ReportDao.callCostStatisticsByRange(scanner);
                }
                case "3" -> {
                    // 3. Thống kê doanh thu theo ngày, tháng, năm
                    ReportDao.callProfitStatisticsByDMY(scanner);
                }
                case "4" -> {
                    // 4. Thống kê doanh thu theo khoảng thời gian
                    ReportDao.callProfitStatisticsByRange(scanner);
                }
                case "5" -> {
                    // 5. Thống kê số nhân viên theo từng trạng thái
                    ReportDao.callEmpStatisticsByStatus();
                }
                case "6" -> {
                    // 6. Thống kê sản phẩm nhập nhiều nhất trong khoảng thời gian
                    ReportDao.callProductStatisticsDesc(scanner, false);
                }
                case "7" -> {
                    // 7. Thống kê sản phẩm nhập ít nhất trong khoảng thời gian
                    ReportDao.callProductStatisticsAsc(scanner, false);
                }
                case "8" -> {
                    // 8. Thống kê sản phẩm xuất nhiều nhất trong khoảng thời gian
                    ReportDao.callProductStatisticsDesc(scanner, true);
                }
                case "9" -> {
                    // 9. Thống kê sản phẩm xuất ít nhất trong khoảng thời gian
                    ReportDao.callProductStatisticsAsc(scanner, true);
                }
                case "10" -> {
                    // 10. Thoát
                    Notification.successMessage("Đã quay về kho");
                }
                default -> Notification.errorMessage("Lệnh nhập phải là số từ 1 - 10, xin hãy nhập lại");
            }
        } while (!choice.equals("10"));
    }
}
