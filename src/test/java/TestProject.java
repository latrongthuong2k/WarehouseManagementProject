
import org.example.Dao.BillDao;
import org.example.Dao.ProductDao;
import org.example.Util.Notification;
import org.example.Util.Validate;

import java.sql.SQLException;
import java.util.Scanner;

import static org.example.Dao.BillDao.getBillId;
import static org.example.Dao.ReportDao.callEmpStatisticsByStatus;

public class TestProject {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            if (ProductDao.checkExistProduct(null, "ádsad"))
                Notification.successMessage("Tồn tại");
            else
                Notification.errorMessage("Ko tồn tại");
//            Validate.isValidDate("10-20-20");
//            callEmpStatisticsByStatus();
//            ReportDao.costStatisticsByDate(scanner);
//            ReportDao.callEmpStatisticsByStatus(scanner);
//            ReportDao.callProductStatisticsAsc(scanner, false);
            BillDao.displayBill(false, "NV02", null);
            System.out.println(getBillId("37"));
//            BillDao.displayBill(false, "1", "0");
//            if (!BillDao.checkEmpIsAuthorizeToBill(null, "billcode23", "1")) {
//                Notification.waringMessage("Bạn không có quyền sửa đổi trên hoá đơn này. Hãy thử lại!");
//            } else
//                System.out.println("ok");

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
