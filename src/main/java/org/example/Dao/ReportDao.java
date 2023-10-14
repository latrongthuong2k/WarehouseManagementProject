package org.example.Dao;

import org.example.Util.ColorText;
import org.example.Util.DbConnection;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

public class ReportDao {

    public static void callCostStatisticsByDMY(Scanner scanner) throws SQLException, ClassNotFoundException {
        do {
            Notification.inputPrint("Chọn các lựa chọn sau để hiển thị bảng thống kê ");
            System.out.println("* 1. Hiển thị bản thống kê theo ngày");
            System.out.println("* 2. Hiển thị bản thống kê theo tháng");
            System.out.println("* 3. Hiển thị bản thống kê theo năm");
            System.out.println("* 4. Quay về menu");
            String choice;
            do {
                choice = scanner.nextLine().trim();
                if (!Validate.isInputValid(choice)) continue;
                if (choice.equalsIgnoreCase("exit") || choice.equals("4")) return;
                if (choice.equals("1") || choice.equals("2") || choice.equals("3")) {
                    break;
                } else {
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn lại (1, 2, hoặc 3) hoặc nhập (4) để quay lại menu:");
                }
            } while (true);  // Assumed validation method, adjust as necessary

            String storedProc = "{call sp_costStatisticsByDYM()}";
            try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
                cs.execute();

                // Move to the correct result set based on user choice
                for (int i = 1; i <= Integer.parseInt(choice); i++) {
                    if (i > 1) {
                        cs.getMoreResults();
                    }
                }

                String titleUpperCase = "◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ BẢNG THỐNG KÊ CHI PHÍ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇";
                System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase + ColorText.RESET);

                ResultSet rs = cs.getResultSet();
                switch (choice) {
                    case "1" -> designTitle("Date ", "TotalCost ");
                    case "2" -> designTitle("Month ", "TotalCost ");
                    case "3" -> designTitle("Year ", "TotalCost ");
                }

                while (rs.next()) {
                    switch (choice) {
                        case "1" -> {
                            String date = rs.getString("Date");
                            double totalCostDay = rs.getDouble("TotalCost");
                            System.out.printf("⎜ %-25s⎜ %-30.2f |\n", date, totalCostDay);
                        }
                        case "2" -> {
                            String month = rs.getString("Month");
                            double totalCostMonth = rs.getDouble("TotalCost");
                            System.out.printf("⎜ %-25s⎜ %-30.2f |\n", month, totalCostMonth);
                        }
                        case "3" -> {
                            String year = rs.getString("Year");
                            double totalCostYear = rs.getDouble("TotalCost");
                            System.out.printf("⎜ %-25s⎜ %-30.2f |\n", year, totalCostYear);
                        }
                    }
                }
                border(31);
            }
        } while (true);
    }

    public static void border(int repeatNum) {
        String slag = "⏤";
        System.out.println(ColorText.WHITE_BRIGHT + "◇" + slag.repeat(repeatNum) + "◇" + ColorText.RESET);
    }

    private static void turnColor(String text) {
        System.out.printf("⎜ " + ColorText.WHITE_BRIGHT + "%-25s" + ColorText.RESET, text);
    }

    private static void designTitle(String fieldName_1, String fieldName_2) {
        border(31);
        turnColor(fieldName_1);
        System.out.printf("⎜ " + ColorText.WHITE_BRIGHT + "%-30s" + ColorText.RESET + " ⎜\n", fieldName_2);
        border(31);
    }


    public static void callCostStatisticsByRange(Scanner scanner) throws SQLException, ClassNotFoundException {
        String startDate;
        String endDate;
        String input;
        do {
            Notification.inputPrint("Nhập ngày bắt đầu (yyyy-MM-dd) hoặc nhập (exit) về menu : ");
            input = scanner.nextLine().trim();
            if (!Validate.isInputValid(input)) continue;
            if (input.equalsIgnoreCase("exit")) return;
            if (!Validate.isValidDate(input)) continue;
            startDate = input;
            break;
        } while (true);
        do {
            Notification.inputPrint("Nhập ngày kết thúc (yyyy-MM-dd) hoặc nhập (exit) về menu : ");
            input = scanner.nextLine().trim();
            if (!Validate.isInputValid(input)) continue;
            if (input.equalsIgnoreCase("exit")) return;
            if (!Validate.isValidDate(input)) continue;
            endDate = input;
            break;
        } while (true);
        String storedProc = "{call sp_CostStatistics_ByRange(?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setString(1, startDate);
            cs.setString(2, endDate);
            ResultSet rs = cs.executeQuery();
            String titleUpperCase = "◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ BẢNG THỐNG KÊ CHI PHÍ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇";
            System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase + ColorText.RESET);

            border(20);
            turnColor("Time range: " + startDate + " -> " + endDate);
            System.out.print(" ⎜ \n");
            border(20);
            if (!rs.isBeforeFirst())
                System.out.printf("⎜ %-37s⎜\n", "Không có dữ liệu");
            else {
                while (rs.next()) {
                    String cost = rs.getString("TotalCost");
                    System.out.printf("⎜ %-37s⎜\n", cost);
                }
            }
            border(20);

        }
    }

    public static void callProfitStatisticsByDMY(Scanner scanner) throws SQLException, ClassNotFoundException {
        do {
            Notification.inputPrint("Chọn các lựa chọn sau để hiển thị bảng thống kê ");
            System.out.println("* 1. Hiển thị bản thống kê doanh thu theo ngày");
            System.out.println("* 2. Hiển thị bản thống kê doanh thu theo tháng");
            System.out.println("* 3. Hiển thị bản thống kê doanh thu theo năm");
            System.out.println("* 4. Quay về menu");
            String choice;
            do {
                choice = scanner.nextLine().trim();
                if (!Validate.isInputValid(choice)) continue;
                if (choice.equalsIgnoreCase("exit") || choice.equals("4")) return;
                if (choice.equals("1") || choice.equals("2") || choice.equals("3")) {
                    break;
                } else {
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn lại (1, 2, hoặc 3) hoặc nhập (4) để quay lại menu:");
                }
            } while (true);  // Assumed validation method, adjust as necessary

            String storedProc = "{call sp_profitStatisticsByDYM()}";
            try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
                cs.execute();

                // Move to the correct result set based on user choice
                for (int i = 1; i <= Integer.parseInt(choice); i++) {
                    if (i > 1) {
                        cs.getMoreResults();
                    }
                }

                String titleUpperCase = "◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ BẢNG THỐNG KÊ DOANH THU ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇";
                System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase + ColorText.RESET);

                ResultSet rs = cs.getResultSet();
                switch (choice) {
                    case "1" -> designTitle("Date ", "TotalProfit ");
                    case "2" -> designTitle("Month ", "TotalProfit ");
                    case "3" -> designTitle("Year ", "TotalProfit ");
                }

                while (rs.next()) {
                    switch (choice) {
                        case "1" -> {
                            String date = rs.getString("Date");
                            double totalCostDay = rs.getDouble("TotalProfit");
                            System.out.printf("⎜ %-25s⎜ %30.2f |\n", date, totalCostDay);
                        }
                        case "2" -> {
                            String month = rs.getString("Month");
                            double totalCostMonth = rs.getDouble("TotalProfit");
                            System.out.printf("⎜ %-25s⎜ %30.2f |\n", month, totalCostMonth);
                        }
                        case "3" -> {
                            String year = rs.getString("Year");
                            double totalCostYear = rs.getDouble("TotalProfit");
                            System.out.printf("⎜ %-25s⎜ %30.2f |\n", year, totalCostYear);
                        }
                    }
                }
                border(31);
            }
        } while (true);
    }

    public static void callProfitStatisticsByRange(Scanner scanner) throws SQLException, ClassNotFoundException {
        String startDate;
        String endDate;
        String input;
        do {
            Notification.inputPrint("Nhập ngày bắt đầu (yyyy-MM-dd) hoặc nhập (exit) về menu : ");
            input = scanner.nextLine().trim();
            if (!Validate.isInputValid(input)) continue;
            if (input.equalsIgnoreCase("exit")) return;
            if (!Validate.isValidDate(input)) continue;
            startDate = input;
            break;
        } while (true);
        do {
            Notification.inputPrint("Nhập ngày kết thúc (yyyy-MM-dd) hoặc nhập (exit) về menu : ");
            input = scanner.nextLine().trim();
            if (!Validate.isInputValid(input)) continue;
            if (input.equalsIgnoreCase("exit")) return;
            if (!Validate.isValidDate(input)) continue;
            endDate = input;
            break;
        } while (true);
        String storedProc = "{call sp_ProfitStatistics_ByRange(?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setString(1, startDate);
            cs.setString(2, endDate);
            ResultSet rs = cs.executeQuery();
            String titleUpperCase = "◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ BẢNG THỐNG KÊ DOANH THU ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇";
            System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase + ColorText.RESET);
            border(20);
            turnColor("Time range: " + startDate + " -> " + endDate);
            System.out.print(" ⎜ \n");
            border(20);
            if (!rs.isBeforeFirst())
                System.out.printf("⎜ %-37s⎜\n", "Không có dữ liệu");
            else {
                while (rs.next()) {
                    String profit = rs.getString("TotalProfit");
                    System.out.printf("⎜ %-37s⎜\n", profit);
                }
            }
            border(20);
        }
    }

    public static void callEmpStatisticsByStatus() throws SQLException, ClassNotFoundException {
        String storedProc = "{call sp_EmployeeStatusStatistics(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();
            String titleUpperCase = "◇ ◇ ◇ ◇ BẢNG THỐNG KÊ SỐ NHÂN VIÊN ◇ ◇ ◇ ◇";
            System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase + ColorText.RESET);
            border(42);
            turnColor(" Hoạt động");
            turnColor(" Nghỉ chế độ");
            System.out.printf(ColorText.WHITE_BRIGHT + "⎜  %-23s" + ColorText.RESET, "Nghỉ việc");
            System.out.print(" ⎜ \n");
            border(42);
            String activeCount = cs.getString(1);
            String temporaryCount = cs.getString(2);
            String quitJob = cs.getString(3);
            System.out.printf("⎜ %-24s ⎜ %-24s ⎜ %-24s ⎜", activeCount, temporaryCount, quitJob);
            System.out.print("\n");
            border(42);
            System.out.println();
        }
    }

    public static void callProductStatisticsAsc(Scanner scanner, Boolean billType) throws SQLException, ClassNotFoundException {
        String startDate;
        String endDate;
        String input;
        do {
            Notification.inputPrint("Nhập ngày bắt đầu (yyyy-MM-dd) hoặc nhập (exit) về menu : ");
            input = scanner.nextLine().trim();
            if (!Validate.isInputValid(input)) continue;
            if (input.equalsIgnoreCase("exit")) return;
            if (!Validate.isValidDate(input)) continue;
            startDate = input;
            break;
        } while (true);
        do {
            Notification.inputPrint("Nhập ngày kết thúc (yyyy-MM-dd) hoặc nhập (exit) về menu : ");
            input = scanner.nextLine().trim();
            if (!Validate.isInputValid(input)) continue;
            if (input.equalsIgnoreCase("exit")) return;
            if (!Validate.isValidDate(input)) continue;
            endDate = input;
            break;
        } while (true);
        String storedProc = "{call sp_ProductStatistics_Asc(?,?,?)}";
        callStatement(startDate, endDate, storedProc, billType, " ít nhất ");
    }

    public static void callProductStatisticsDesc(Scanner scanner, Boolean billType) throws SQLException, ClassNotFoundException {
        String startDate;
        String endDate;
        String input;
        do {
            Notification.inputPrint("Nhập ngày bắt đầu (yyyy-MM-dd) hoặc nhập (exit) về menu : ");
            input = scanner.nextLine().trim();
            if (!Validate.isInputValid(input)) continue;
            if (input.equalsIgnoreCase("exit")) return;
            if (!Validate.isValidDate(input)) continue;
            startDate = input;
            break;
        } while (true);
        do {
            Notification.inputPrint("Nhập ngày kết thúc (yyyy-MM-dd) hoặc nhập (exit) về menu : ");
            input = scanner.nextLine().trim();
            if (!Validate.isInputValid(input)) continue;
            if (input.equalsIgnoreCase("exit")) return;
            if (!Validate.isValidDate(input)) continue;
            endDate = input;
            break;
        } while (true);
        String storedProc = "{call sp_ProductStatistics_Desc(?,?,?)}";
        callStatement(startDate, endDate, storedProc, billType, " nhiều nhất ");
    }

    private static void callStatement(String startDate,
                                      String endDate,
                                      String storedProc,
                                      Boolean billType,
                                      String ascOrDesc) throws SQLException, ClassNotFoundException {
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setString(1, startDate);
            cs.setString(2, endDate);
            cs.setBoolean(3, billType);
            ResultSet rs = cs.executeQuery();
            String title = " ◇ ◇ ◇ ◇ BẢNG THỐNG KÊ SỐ SẢN PHẨM " +
                    BillDao.getTypeName(billType) + ascOrDesc + " ◇ ◇ ◇ ◇";
            System.out.println(ColorText.CYAN_BRIGHT + title.toUpperCase() + ColorText.RESET);
            border(28);
            turnColor(" Product Name");
            turnColor(" Amount");
            System.out.print(" ⎜ \n");
            border(28);
            if (!rs.isBeforeFirst())
                System.out.printf("⎜ %-25s⎜ %-25s ⎜\n", "Không có dữ liệu", "không có dữ liệu");
            else {
                while (rs.next()) {
                    String productName = rs.getString("Product_Name");
                    String totalAmount = rs.getString("TotalAmount");
                    System.out.printf("⎜ %-25s⎜ %-25s ⎜\n", productName, totalAmount);
                }
            }
            border(28);
        }
    }
}
