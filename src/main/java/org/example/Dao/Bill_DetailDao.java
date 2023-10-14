package org.example.Dao;

import org.example.Model.Bill_Detail;
import org.example.Util.ColorText;
import org.example.Util.DbConnection;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

import static org.example.Dao.BillDao.getTypeName;
import static org.example.Dao.BillDao.space;

public class Bill_DetailDao {
    public static void displayBillDetail(Scanner scanner, Boolean billType, String productId) throws SQLException, ClassNotFoundException {
        BillDao.displayBill(billType, null, null);
        long parseBillId;
        String input;
        do {
            Notification.inputPrint("Nhập (Bill Id) bất kì có trong bảng phiếu " + BillDao.getTypeName(billType) +
                    " để hiển thị chi tiết bill, hoặc nhập (exit) để thoát");
            input = scanner.nextLine().trim();
            if (!Validate.isInputValid(input)) continue;
            if (input.equalsIgnoreCase("exit")) return;
            if (!BillDao.isNumeric(input)) {
                Notification.errorMessage("Bill_Id phải là các số nguyên, hãy thử lại!");
                continue;
            } else
                parseBillId = Long.parseLong(input);
            // check tồn tại parseBillId
            if (!BillDao.checkExistsBill(parseBillId, null, billType)) {
                Notification.errorMessage("Không tồn tại Bill ở trong phiếu " + BillDao.getTypeName(billType) + ", hãy thử lại!");
                continue;
            }
            break;
        } while (true);
        // call GetBillDetails
        String storedProc = "{call GetBillDetails(?,?,?)}";
        try (
                CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setLong(1, parseBillId);
            cs.setBoolean(2, billType);
            cs.setString(3, productId);
            cs.execute();
            try (ResultSet rs = cs.getResultSet()) {
                String titleUpperCase = space(5) +
                        "◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ " + "Bảng chi tiết phiếu " + getTypeName(billType) + " ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇";
                System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase.toUpperCase() + ColorText.RESET);
                designTableForResultSet(rs);
            }
        }

    }

    private static void designPageForResultSet(ResultSet rs) throws SQLException {
        if (!rs.isBeforeFirst()) {
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-20s", "No Data");
            System.out.printf("⎜ %-15s", "No Data");
            System.out.printf("⎜ %-21s |\n", "No Data");
        } else
            while (rs.next()) {
                // Hiển thị thông tin sản phẩm
                System.out.printf("⎜ %-20s", rs.getString("Bill_Id"));
                System.out.printf("⎜ %-20s", rs.getString("Product_Id"));
                System.out.printf("⎜ %-15s", rs.getString("Quantity"));
                System.out.printf("⎜ %-21s |\n", rs.getString("Price"));
            }
    }

    public static void border() {
        String slag = "⏤";
        System.out.println(ColorText.WHITE_BRIGHT + "◇" + slag.repeat(44) + "◇" + ColorText.RESET);
    }

    private static void designTableForResultSet(ResultSet rs) throws SQLException {
        // Title
        designTitle();
        // page
        designPageForResultSet(rs);
        border();
        System.out.println(); // cách ra
    }

    private static void designTitle() {
        // Title
        border();
        turnColor("Bill_Id ", 20);
        turnColor("Product_Id ", 20);
        turnColor("Quantity ", 15);
        System.out.printf("⎜ " + ColorText.WHITE_BRIGHT + "%-21s" + ColorText.RESET + " ⎜\n", "Price ");
        border();
    }

    private static void turnColor(String text, int with) {
        System.out.printf("⎜ " + ColorText.WHITE_BRIGHT + "%-" + with + "s" + ColorText.RESET, text);
    }

    public static void createBillDetail(Bill_Detail billDetail) throws SQLException, ClassNotFoundException {
        String storedProc = "{call CreateBillDetail(?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            cs.setLong(1, billDetail.getBillId());
            cs.setString(2, billDetail.getProductId());
            cs.setInt(3, billDetail.getQuantity());
            cs.setFloat(4, billDetail.getPrice());
            cs.execute();
        }
    }

    public static boolean checkValidQuantityToSales(Long billId, String billCode) throws SQLException, ClassNotFoundException {
        String storedProc = "{call checkValidQuantityToSales(?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            if (billId == null) {
                cs.setNull(1, Types.BIGINT);
                cs.setString(2, billCode);
            } else {
                String input = String.valueOf(billId);
                cs.setLong(1, billId);
                cs.setString(2, input);
            }
            cs.registerOutParameter(3, Types.BOOLEAN);
            cs.execute();
            return cs.getBoolean(3);
        }
    }

    public static void searchBillDetail(Long billId, String billCode, boolean billType, String empId) throws SQLException, ClassNotFoundException {
        String storedProc = "{call searchBillDetail(?,?,?,?)}";
        try (CallableStatement cs = DbConnection.getConnection().prepareCall(storedProc)) {
            if (billId == null) {
                cs.setNull(1, Types.BIGINT);
                cs.setString(2, billCode);
            } else {
                String input = String.valueOf(billId);
                cs.setLong(1, billId);
                cs.setString(2, input);
            }
            cs.setBoolean(3, billType);
            cs.setString(4, empId);
            cs.execute();
            try (ResultSet rs = cs.getResultSet()) {
                String titleUpperCase = space(5) +
                        "◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ " + "Bảng chi tiết phiếu " + getTypeName(billType) +
                        " của hoá đơn có ID : " + billId + " ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇ ◇";
                System.out.println(ColorText.CYAN_BRIGHT + titleUpperCase.toUpperCase() + ColorText.RESET);

            }
        }
    }
}
