package org.example.Model;

import org.example.Dao.BillDao;
import org.example.Dao.Bill_DetailDao;
import org.example.Dao.ProductDao;
import org.example.Util.Notification;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import static org.example.Util.Validate.isInputValid;

public class Bill_Detail {
    private Long Bill_Detail_Id;
    private Long billId;
    private String productId;
    private Integer quantity;
    private Float price;
    private Boolean type;

    public Bill_Detail() {
    }

    public Long getBill_Detail_Id() {
        return Bill_Detail_Id;
    }

    public void setBill_Detail_Id(Long bill_Detail_Id) {
        Bill_Detail_Id = bill_Detail_Id;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public void inputData(Scanner scanner, String nameAction) throws SQLException, ClassNotFoundException {
        String input;

        // Mã bill
        if (billId == null) {
            Long Id;
            do {
                Notification.inputPrint("Nhập bill ID : ");
                input = scanner.nextLine().trim();
                if (!isInputValid(input)) continue;
                try {
                    Id = Long.parseLong(input);
                    if (!BillDao.checkExistsBill(Id, null, type)) {
                        Notification.errorMessage("Không tồn tại Bill ở trong phiếu "
                                + BillDao.getTypeName(type) + ", hãy thử lại!");
                        continue;
                    }
                    break;
                } catch (InputMismatchException e) {
                    Notification.errorMessage("ID bill phải là số, xin hãy nhập lại cho đúng");
                }
            } while (true);
            setBillId(Id);
            Notification.successMessage("✓ SUCCESS!");
        }

        // Nhập mã product
        if (productId == null) {
            do {
                Notification.inputPrint("Nhập ID của sản phẩm : ");
                input = scanner.nextLine().trim(); // input productId
                if (!isInputValid(input)) continue;
                // Nếu sản phẩm với input (ID) tồn tại thì
                // kiểm tra trong bảng bill detail có dựa theo Bill id
                // có sản phẩm nào liên kết với Bill id không? true : false ⬇
                if (!ProductDao.checkExistProduct(input, null)) {
                    Notification.errorMessage("Không tồn tại sản phẩm với Product_Id : " + input);
                    continue;
                }
                if (!BillDao.checkExistBillDetail(this.billId, input)) {
                    Notification.errorMessage("Không tồn tại bill_detail có id:" + billId + " và Product_Id : " + input);
                    continue;
                }
                break;
            }
            while (true);
            setProductId(input);
            Notification.successMessage("✓ SUCCESS!");
        }
//        Bill_DetailDao.displayBillDetail(scanner, type, this.getProductId());

        // Nhập số lượng
        int quantity;
        do {
            Notification.inputPrint("Nhập số lượng để " + BillDao.getTypeName(type) + " : ");
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            try {
                quantity = Integer.parseInt(input);
                if (quantity < 0)
                    Notification.errorMessage("Số lượng phải lớn hơn 0 ");
                break;
            } catch (InputMismatchException e) {
                Notification.errorMessage("Số lượng phải là số");
            }
        } while (true);
        setQuantity(quantity);
        Notification.successMessage("✓ SUCCESS!");

        // Nhập giá xuất / nhập
        float price;
        do {
            Notification.inputPrint("Nhập giá " + BillDao.getTypeName(type) + " : ");
            input = scanner.nextLine().trim();
            if (!isInputValid(input)) continue;
            try {
                price = Float.parseFloat(input);
                if (price < 0)
                    Notification.errorMessage("Giá sản phẩm phải lớn 0");
                else break;
            } catch (InputMismatchException e) {
                Notification.errorMessage("Giá" + BillDao.getTypeName(type) + " : " + "phải phải là số nguyên");
            }
        } while (true);

        setPrice(price);
        Notification.successMessage("✓ SUCCESS!");
    }
}
