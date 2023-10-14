-- Database
create schema projectMD3_Db character set utf8mb4 collate utf8mb4_vietnamese_ci;
use projectMD3_Db;
-- Table product
create table Product
(
    Product_Id     char(5) primary key,
    Product_Name   varchar(150) not null unique,
    Manufacturer   varchar(200) not null,
    Created        date                  default (curdate()),
    Batch          smallint     not null,
    Quantity       int          not null default 0,
    Product_Status bit                   default 1
);

-- Table Employee
create table Employee
(
    Emp_Id        char(5) primary key,
    Emp_Name      varchar(100) not null unique,
    Birth_Of_Date date,
    Email         varchar(100) not null,
    Phone         varchar(100) not null,
    Address       text         not null,
    Emp_Status    smallint     not null
);
-- Table Account
create table Account
(
    Acc_id     int primary key auto_increment,
    User_name  varchar(30) not null unique,
    Password   varchar(30) not null,
    Permission bit default 1,
    Emp_id     char(5)     not null unique,
    Acc_status bit default 1
);
alter table Account
    add constraint Account_fk
        foreign key (Emp_id) references Employee (Emp_Id);
-- Table Bill
create table Bill
(
    Bill_id        bigint primary key auto_increment,
    Bill_Code      varchar(10) not null,
    Bill_Type      bit         not null,
    Emp_id_created char(5)     not null,
    Created        date                 default (curdate()),
    Emp_id_auth    char(5)     null,
    Auth_date      date                 default (curdate()),
    Bill_Status    smallint    not null default 0
);
alter table Bill
    add constraint fk_employeeId_import_export
        foreign key (Emp_id_created) references Employee (Emp_Id),
    add constraint fk_employeeId_auth
        foreign key (Emp_id_auth) references Employee (Emp_Id);

-- Table Bill_Detail
create table Bill_Detail
(
    Bill_Detail_Id bigint primary key auto_increment,
    Bill_Id        bigint  not null,
    Product_Id     char(5) not null,
    Quantity       int     not null check ( Quantity > 0 ),
    Price          float   not null check ( Price > 0 )
);
alter table Bill_Detail
    add constraint fk_billId
        foreign key (Bill_Id) references Bill (Bill_id),
    add constraint fk_ProductId
        foreign key (Product_Id) references Product (Product_Id);

-- <><><><><><> product management <><><><><><>
-- show list product
drop procedure ListProducts;
DELIMITER //
CREATE PROCEDURE ListProducts(IN offset INT, OUT total_count INT)
BEGIN
    -- Query lấy tổng số bản ghi phù hợp
    SELECT COUNT(*)
    INTO total_count
    FROM Product;

    -- Query lấy kết quả phân trang
    SELECT *
    FROM Product
    ORDER BY Product_Name
    LIMIT 10 OFFSET offset;
END //
DELIMITER ;
-- **********
-- kiểm tra tồn tại
DELIMITER //
CREATE PROCEDURE checkExistProduct(
    IN p_Product_Id CHAR(5),
    IN p_Product_Name VARCHAR(150),
    OUT isExists BOOLEAN
)
BEGIN
    SET isExists = FALSE;
    SELECT EXISTS(SELECT 1
                  FROM Product
                  WHERE (p_Product_Id IS NOT NULL AND Product_Id = p_Product_Id)
                     OR (p_Product_Name IS NOT NULL AND Product_Name = p_Product_Name)
                     OR (p_Product_Id IS NOT NULL AND p_Product_Name IS NOT NULL AND Product_Id = p_Product_Id AND
                         Product_Name = p_Product_Name))
    INTO isExists;
END //
DELIMITER ;

-- get product name by id


-- **********

-- add product
DELIMITER //
CREATE PROCEDURE AddProduct(IN p_Product_Id CHAR(5), IN p_Product_Name VARCHAR(150), IN p_Manufacturer VARCHAR(200),
                            IN p_Batch SMALLINT)
BEGIN
    INSERT INTO Product(Product_Id, Product_Name, Manufacturer, Batch)
    VALUES (p_Product_Id, p_Product_Name, p_Manufacturer, p_Batch);
END //
DELIMITER ;

-- update sản phẩm
DELIMITER //
CREATE PROCEDURE UpdateProduct(IN p_Product_Id CHAR(5), IN p_Product_Name VARCHAR(150), IN p_Manufacturer VARCHAR(200),
                               IN p_Batch SMALLINT)
BEGIN
    UPDATE Product
    SET Product_Name = p_Product_Name,
        Manufacturer = p_Manufacturer,
        Batch        = p_Batch
    WHERE Product_Id = p_Product_Id;
END //
DELIMITER ;

-- tìm kiếm sản phẩm
drop procedure searchProduct;
DELIMITER //
CREATE PROCEDURE searchProduct(IN p_searchInput VARCHAR(150), IN offset INT, OUT total_count INT)
BEGIN
    -- Query lấy tổng số bản ghi phù hợp
    SELECT COUNT(*)
    INTO total_count
    FROM Product
    WHERE Product_Name LIKE CONCAT('%', p_searchInput, '%')
       OR Product_Id LIKE CONCAT('%', p_searchInput, '%');

    -- Query lấy kết quả phân trang
    SELECT *
    FROM Product
    WHERE Product_Name LIKE CONCAT('%', p_searchInput, '%')
       OR Product_Id LIKE CONCAT('%', p_searchInput, '%')
    LIMIT 10 OFFSET offset;
END //
DELIMITER ;

-- Cập nhật trạng thái sp
DELIMITER //
CREATE PROCEDURE UpdateProductStatus(IN p_Product_Id CHAR(5), IN p_Product_Status BIT)
BEGIN
    UPDATE Product
    SET Product_Status = p_Product_Status
    WHERE Product_Id = p_Product_Id;
END //
DELIMITER ;

-- lấy id product
drop procedure getIdProduct;
DELIMITER //
CREATE PROCEDURE getIdProduct(IN p_NameProduct CHAR(150))
BEGIN
    select Product_Id from Product where Product_Name = p_NameProduct;
end //
DELIMITER ;

-- lấy product name
drop procedure getNameProduct;
DELIMITER //
CREATE PROCEDURE getNameProduct(IN p_idProduct CHAR(5))
BEGIN
    select Product_Name from Product where Product_Id = p_idProduct;
end //
DELIMITER ;

-- checkActiveProdcut
drop procedure isActiveProduct;
DELIMITER //
CREATE PROCEDURE isActiveProduct(IN p_idProduct CHAR(5), IN p_productName varchar(150), out valid boolean)
BEGIN
    select Product_Status
    into valid
    from Product
    where Product_Id = p_idProduct
       or Product_Name = p_productName;
end //
DELIMITER ;

-- <><><><><><> Employee manager <><><><><><>
drop procedure ListEmployees;
DELIMITER //
CREATE PROCEDURE ListEmployees(IN offset INT, OUT total_count INT)
BEGIN
    -- Query lấy tổng số bản ghi phù hợp
    SELECT COUNT(*)
    INTO total_count
    FROM Employee;

    -- Query lấy kết quả phân trang
    SELECT *
    FROM Employee
    ORDER BY Emp_Name
    LIMIT 10 OFFSET offset;
END //
DELIMITER ;

-- kiểm tra tồn tại
DELIMITER //
CREATE PROCEDURE checkExistEmp(
    IN p_Emp_Id CHAR(5),
    IN p_Emp_Name VARCHAR(150),
    OUT isExists BOOLEAN
)
BEGIN
    SET isExists = FALSE;
    SELECT EXISTS(SELECT 1
                  FROM Employee
                  WHERE (p_Emp_Id IS NOT NULL AND Emp_Id = p_Emp_Id)
                     OR (p_Emp_Name IS NOT NULL AND Emp_Name = p_Emp_Name)
                     OR (p_Emp_Id IS NOT NULL AND p_Emp_Name IS NOT NULL AND Emp_Id = p_Emp_Id AND
                         Emp_Name = p_Emp_Name))
    INTO isExists;
END //
DELIMITER ;

-- lấy empName
DELIMITER //
CREATE PROCEDURE getNameEmpOrStatusBaseOnActionInput(IN id CHAR(5), in p_Action varchar(20))
BEGIN
    if (p_Action = 'status') then
        select Emp_Status from Employee where Emp_Id = id;
    end if;
    if (p_Action = 'name') then
        select Emp_Name from Employee where Emp_Id = id;
    end if;
end //
DELIMITER ;


-- add employee
DELIMITER //
CREATE PROCEDURE AddEmployee(IN p_Emp_Id CHAR(5), IN p_Emp_Name VARCHAR(100), IN p_Birth_Of_Date DATE,
                             IN p_Email VARCHAR(100), IN p_Phone VARCHAR(100), IN p_Address TEXT,
                             IN p_Emp_Status SMALLINT)
BEGIN
    INSERT INTO Employee(Emp_Id, Emp_Name, Birth_Of_Date, Email, Phone, Address, Emp_Status)
    VALUES (p_Emp_Id, p_Emp_Name, p_Birth_Of_Date, p_Email, p_Phone, p_Address, p_Emp_Status);
END //
DELIMITER ;

-- update employee all info
DELIMITER //
CREATE PROCEDURE UpdateEmployee(IN p_Emp_Id CHAR(5), IN p_Emp_Name VARCHAR(100), IN p_Birth_Of_Date DATE,
                                IN p_Email VARCHAR(100), IN p_Phone VARCHAR(100), IN p_Address TEXT,
                                IN p_Emp_Status SMALLINT)
BEGIN
    UPDATE Employee
    SET Emp_Name      = p_Emp_Name,
        Birth_Of_Date = p_Birth_Of_Date,
        Email         = p_Email,
        Phone         = p_Phone,
        Address       = p_Address,
        Emp_Status    = p_Emp_Status
    WHERE Emp_Id = p_Emp_Id;
END //
DELIMITER ;

-- update employee status
DELIMITER //
CREATE PROCEDURE UpdateEmployeeStatus(IN p_Emp_Id CHAR(5), IN p_Emp_Status SMALLINT)
BEGIN
    UPDATE Employee SET Emp_Status = p_Emp_Status WHERE Emp_Id = p_Emp_Id;
    IF p_Emp_Status = 1 OR p_Emp_Status = 2 THEN -- Tự động chuyển acc thành block nếu 1 nghỉ chế độ / 2 nghỉ việc
        UPDATE Account SET Acc_status = 0 WHERE Emp_id = p_Emp_Id;
    END IF;
END //
DELIMITER ;

-- find employee
DELIMITER //
CREATE PROCEDURE SearchEmployee(IN search_query VARCHAR(100), IN offset INT, OUT total_count INT)
BEGIN
    -- Query lấy tổng số bản ghi phù hợp
    SELECT COUNT(*)
    INTO total_count
    FROM Employee
    WHERE Emp_Id LIKE CONCAT('%', search_query, '%')
       OR Emp_Name LIKE CONCAT('%', search_query, '%');

    SELECT *
    FROM Employee
    WHERE Emp_Id LIKE CONCAT('%', search_query, '%')
       OR Emp_Name LIKE CONCAT('%', search_query, '%')
    ORDER BY Emp_Name
    LIMIT 10 OFFSET offset;
END //
DELIMITER ;

-- <><><><><><> Account manager <><><><><><>

-- show list accounts
drop procedure ListAccounts;
DELIMITER //
CREATE PROCEDURE ListAccounts(IN offset INT, OUT total_count INT)
BEGIN
    -- Query lấy tổng số bản ghi phù hợp
    SELECT COUNT(*)
    INTO total_count
    FROM Account;

    -- Query lấy kết quả phân trang
    SELECT a.Acc_id, a.User_name, e.Emp_Name, a.Permission, a.Acc_status
    FROM Account a
             JOIN Employee e ON a.Emp_id = e.Emp_Id
    ORDER BY a.User_name
    LIMIT 10 OFFSET offset;
END //
DELIMITER ;

-- create account
drop procedure CreateAccount;
DELIMITER //
CREATE PROCEDURE CreateAccount(IN p_UserName VARCHAR(30),
                               IN p_Password VARCHAR(30),
                               p_Permission bit,
                               IN p_Emp_Id CHAR(5),
                               p_acc_Status bit)
BEGIN
    INSERT INTO Account(User_name, Password, Permission, Emp_id, Acc_status)
    VALUES (p_UserName, p_Password, p_Permission, p_Emp_Id, p_acc_Status);
END //
DELIMITER ;

-- update account status
drop procedure UpdateAccountStatus;
DELIMITER //
CREATE PROCEDURE UpdateAccountStatus(IN p_AccName varchar(30), IN p_Acc_status BIT)
BEGIN
    UPDATE Account
    SET Acc_status = p_Acc_status
    WHERE User_name = p_AccName;
END //
DELIMITER ;

-- update all account status ( khi tìm kiếm )
drop procedure UpdateFoundAllAccountStatus;
DELIMITER //
CREATE PROCEDURE UpdateFoundAllAccountStatus(key_word varchar(100), p_Acc_status BIT)
BEGIN
    UPDATE Account a JOIN Employee e ON a.Emp_id = e.Emp_Id
    SET a.Acc_status = p_Acc_status
    WHERE e.Emp_Name LIKE CONCAT('%', key_word, '%')
       OR a.User_name LIKE CONCAT('%', key_word, '%');
end //
DELIMITER ;

# call UpdateFoundAllAccountStatus ('ac', 0);
# select * from Account a JOIN Employee e ON a.Emp_id = e.Emp_Id
# where e.Emp_Id LIKE CONCAT('%', 'ac', '%')
#    OR a.User_name LIKE CONCAT('%', 'ac', '%');

-- Tìm kiếm tk
drop procedure SearchAccount;
DELIMITER //
CREATE PROCEDURE SearchAccount(IN p_SearchTerm VARCHAR(100), IN offset INT, OUT total_count INT)
BEGIN
    -- Query lấy tổng số bản ghi phù hợp
    SELECT COUNT(*)
    INTO total_count
    FROM Account a
             JOIN Employee e ON a.Emp_id = e.Emp_Id
    WHERE a.User_name LIKE CONCAT('%', p_SearchTerm, '%')
       OR e.Emp_Name LIKE CONCAT('%', p_SearchTerm, '%');

    -- Query lấy kết quả phân trang
    SELECT a.Acc_id, a.User_name, e.Emp_Name, a.Permission, a.Acc_status
    FROM Account a
             JOIN Employee e ON a.Emp_id = e.Emp_Id
    WHERE a.User_name LIKE CONCAT('%', p_SearchTerm, '%')
       OR e.Emp_Name LIKE CONCAT('%', p_SearchTerm, '%')
    ORDER BY e.Emp_Name
    LIMIT 10 OFFSET offset;
END //
DELIMITER ;

-- kiểm tra duy nhất emp_id trên account
DELIMITER //
CREATE PROCEDURE isEmpIdUnique(p_emp_Id char(5))
begin
    select * from Account a where a.Emp_id = p_emp_Id;
end //
DELIMITER ;

-- <><><><><><> RECEIPT/BILL manager <><><><><><>

-- list bills
drop procedure GetAllBills;
call GetAllBills(false,'NV02' , null);
call GetAllBills(true, null, null);

DELIMITER //
CREATE PROCEDURE GetAllBills(IN p_bill_Type BIT, IN p_emp_id CHAR(5), IN p_bill_status CHAR)
BEGIN
    DECLARE statusNumber SMALLINT;
    SET statusNumber = CASE p_bill_status
                           WHEN '0' THEN 0
                           WHEN '1' THEN 1
                           WHEN '2' THEN 2
                           WHEN null then null
        END;
    IF statusNumber IS NULL THEN
        -- Nếu cả emp_id và bill_status đều NULL, chỉ lọc theo bill_type
        SELECT *
        FROM Bill
        WHERE Bill_Type = p_bill_Type AND (p_emp_id IS NULL OR Emp_id_created = p_emp_id);
    ELSE
        -- Nếu ít nhất một trong hai tham số không phải NULL, lọc theo tất cả các tham số
        SELECT *
        FROM Bill
        WHERE Bill_Type = p_bill_Type
          AND (p_emp_id IS NULL OR Emp_id_created = p_emp_id)
          AND (Bill_Status = statusNumber);
    END IF;
END //
DELIMITER ;

-- create bill
drop procedure CreateBill;
DELIMITER //
CREATE PROCEDURE CreateBill(IN p_Bill_Code VARCHAR(10), IN p_Bill_Type BIT, IN p_Emp_id_created CHAR(5))
BEGIN
    INSERT INTO Bill(Bill_Code, Bill_Type, Emp_id_created, Auth_date)
    VALUES (p_Bill_Code, p_Bill_Type, p_Emp_id_created, NULL);
END //
DELIMITER ;

-- check exists bill
drop procedure checkExistBill;
call checkExistBill(null, 'bhdaX12', 1, @outcpTest);
select @outcpTest;
DELIMITER //
CREATE PROCEDURE checkExistBill(
    IN p_Bill_Id BIGINT,
    IN p_bill_Code VARCHAR(10),
    IN p_bill_Type BOOLEAN,
    OUT isExists BOOLEAN
)
BEGIN
    SET isExists = EXISTS(SELECT 1
                          FROM Bill e
                          WHERE (e.Bill_Id = p_Bill_Id OR e.Bill_Code = p_bill_Code)
                            AND e.Bill_Type = p_bill_Type);

END //
DELIMITER ;
# call checkExistBill(1,'1234',false, @check);
# select @`check`;

-- check Exists bill_detail depend on bill_id vs productId
DELIMITER //
CREATE PROCEDURE CheckExistBillDetail(
    IN p_Bill_Id BIGINT,
    IN p_Product_Id char(5),
    OUT isExists BOOLEAN
)
BEGIN
    SET isExists = FALSE;
    SELECT EXISTS(SELECT 1
                  FROM Bill_Detail e
                  WHERE p_Bill_Id = e.Bill_id
                    and e.Product_Id = p_Product_Id)
    INTO isExists;
END //
DELIMITER ;

# drop procedure getBillInfoById;
# DELIMITER //
# CREATE PROCEDURE getBillInfoById(
#     IN p_Bill_Id BIGINT,
#     out p_Bill_Code VARCHAR(10), out p_Bill_Type BIT,
#     out p_Emp_id_created CHAR(5), out p_Emp_id_auth CHAR(5)
# )
# BEGIN
#     select b.Bill_Code,
#            b.Bill_Type,
#            b.Emp_id_created,
#            b.Emp_id_auth
#     into p_Bill_Code,p_Bill_Type,p_Emp_id_created,p_Emp_id_auth
#     from Bill b
#     where Bill_id = p_Bill_Id;
# END //
# DELIMITER ;

-- update Bill info
drop procedure UpdateBill;
DELIMITER //
CREATE PROCEDURE UpdateBill(IN p_Bill_Id BIGINT, IN p_Bill_Code VARCHAR(10), IN p_bill_status smallint,
                            IN p_Emp_id_created CHAR(5), in p_bill_type bit, IN p_emp_id CHAR(5))
BEGIN
    DECLARE billStatus_if SMALLINT;
    SELECT Bill_Status INTO billStatus_if FROM Bill WHERE Bill_id = p_Bill_Id and Bill_Type = p_bill_type;
    IF billStatus_if = 0 OR billStatus_if = 2 THEN
        UPDATE Bill
        SET Bill_Code      = p_Bill_Code,
            Emp_id_created = p_Emp_id_created,
            Bill_Status    = p_bill_status,
            Emp_id_created = p_emp_id
        WHERE (Bill_id = p_Bill_Id or Bill_Code = p_Bill_Code)
          AND Bill_Type = p_bill_type;
    END IF;
END //
DELIMITER ;

# Get bill id
drop procedure Get_Bill_Id;
DELIMITER //
CREATE PROCEDURE Get_Bill_Id(p_bill_code varchar(10))
BEGIN
    select Bill_id from Bill where Bill_Code = p_bill_code;
END //
DELIMITER ;
# call Get_Bill_Id('1234');

-- get detail bill depend on Bill_type
call GetBillDetails(12, false);
drop procedure GetBillDetails;
DELIMITER //
CREATE PROCEDURE GetBillDetails(IN p_Bill_Id BIGINT, IN p_bill_Type BIT, IN p_Product_Id char(5))
BEGIN
    if p_Product_Id is null then
        SELECT b_d.Bill_Id, b_d.Product_Id, b_d.Quantity, b_d.Price
        FROM Bill_Detail b_d
                 JOIN Bill b ON b.Bill_id = b_d.Bill_Id
        WHERE b_d.Bill_Id = p_Bill_Id
          and b.Bill_Type = p_bill_Type;
    else
        SELECT b_d.Bill_Id, b_d.Product_Id, b_d.Quantity, b_d.Price
        FROM Bill_Detail b_d
                 JOIN Bill b ON b.Bill_id = b_d.Bill_Id
        WHERE b_d.Bill_Id = p_Bill_Id
          and Product_Id = p_Product_Id
          and b.Bill_Type = p_bill_Type;
    end if;

END //
DELIMITER ;

-- create bill_detail
drop procedure CreateBillDetail;
DELIMITER //
CREATE PROCEDURE CreateBillDetail(IN p_bill_id BIGINT, p_product_id char(5), p_quantity int,
                                  p_price float)
BEGIN
    INSERT INTO Bill_Detail(Bill_Id, Product_Id, Quantity, Price)
    VALUES (p_bill_id, p_product_id, p_quantity, p_price);
end;
DELIMITER ;

-- update bill_detail
drop procedure UpdateBillDetail;
DELIMITER //
CREATE PROCEDURE UpdateBillDetail(IN p_Bill_Id BIGINT, IN p_Product_Id char(5),
                                  IN p_Quantity int, IN p_Price float)
BEGIN
    UPDATE Bill_Detail
    set Quantity = p_Quantity,
        Price    = p_Price
    where Bill_Id = p_Bill_Id
      and Product_Id = p_Product_Id;
END //
DELIMITER ;

-- search bill detail
drop procedure searchBillDetail;
DELIMITER //
CREATE PROCEDURE searchBillDetail(IN p_Bill_Id BIGINT, in p_Bill_Code VARCHAR(10), in p_bill_type boolean,
                                  in p_emp_Id char(5))
BEGIN
    select *
    from Bill_Detail bd join Bill B on bd.Bill_Id = B.Bill_id
    where (bd.Bill_Id = p_Bill_Id
        or Bill_Code = p_Bill_Code)
      and Bill_Type = p_bill_type
      and Emp_id_created = p_emp_Id;
END //
DELIMITER ;

-- check BillStatus is is Approved ?
drop procedure checkBillStatusIsApproved;
DELIMITER //
CREATE PROCEDURE checkBillStatusIsApproved(IN p_Bill_Id BIGINT, IN p_Bill_Code varchar(10),
                                           OUT isNotApproved boolean)
BEGIN
    set isNotApproved = false;
    select exists(select 1
                  from Bill
                  where (Bill_id = p_Bill_Id or Bill_Code = p_Bill_Code)
                    and (Bill_Status = 1 or Bill_Status = 0))
    into isNotApproved;
end //
DELIMITER ;

-- check quantity of bill_detail is greater quantity of product ?
drop procedure checkValidQuantityToSales;
DELIMITER //
CREATE PROCEDURE checkValidQuantityToSales(in p_bill_id bigint, p_bill_code varchar(10), out isValid boolean)
BEGIN
    SET isValid = NOT EXISTS (SELECT 1
                              FROM Product p
                                       JOIN Bill_Detail bd ON bd.Product_Id = p.Product_Id
                                       JOIN Bill b ON b.Bill_id = bd.Bill_Id
                              WHERE (bd.Bill_Id = p_bill_id OR b.Bill_Code = p_bill_code)
                                AND p.Quantity < bd.Quantity);
end //
DELIMITER ;
-- approve bill
drop procedure ApproveBill;
DELIMITER //
CREATE PROCEDURE ApproveBill(
    IN p_Bill_Id BIGINT,
    IN p_Bill_Code VARCHAR(10),
    IN p_bill_type BIT,
    IN p_Em_Id_Auth varchar(5)
)
BEGIN
    --  lấy billId và check trạng thái vào một câu truy vấn
    SELECT Bill_id, Bill_Status
    INTO @billId, @billStatus
    FROM Bill
    WHERE (Bill_id = p_Bill_Id OR Bill_Code = p_Bill_Code)
      AND Bill_Type = p_bill_type;

    -- update Status và ngày duyệt nếu billStatus = 0
    -- thực ra bên ngoài cũng có validate trước
    IF @billStatus = 0 THEN
        UPDATE Bill
        SET Bill_Status = 2, -- 2: Duyệt
            Auth_date   = CURRENT_DATE,
            Emp_id_auth = p_Em_Id_Auth
        WHERE Bill_id = @billId;

        -- Cập nhật số lượng sản phẩm
        UPDATE Product p
            JOIN Bill_Detail bd ON bd.Product_Id = p.Product_Id
        -- bill type false + / true -
        SET p.Quantity = p.Quantity + IF(p_bill_type, -bd.Quantity, bd.Quantity)
        WHERE bd.Bill_Id = @billId;
    END IF;
END //
DELIMITER ;


-- Get auth bill
DELIMITER //
CREATE PROCEDURE GetAuthIdBill(p_Bill_Id BIGINT, p_Bill_Code varchar(10))
BEGIN
    select Emp_id_auth from Bill b where b.Bill_id = p_Bill_Id or b.Bill_Code = p_Bill_Code;
END //
DELIMITER ;


-- find bill
drop procedure SearchBill;
DELIMITER //
CREATE PROCEDURE SearchBill(p_Bill_Id BIGINT, IN p_Bill_Code VARCHAR(10), p_billType bit)
BEGIN
    SELECT *
    FROM Bill
    WHERE (Bill_Id = p_Bill_Id OR Bill_Code LIKE CONCAT('%', p_Bill_Code, '%'))
      AND Bill_Type = p_billType;

END //
DELIMITER ;

-- <><><><><><> REPORT manager <><><><><><>

-- 1. Thống kê chi phí theo ngày, tháng, năm
-- 0 false nhập
-- 1 true xuất
drop procedure sp_costStatisticsByDYM;
DELIMITER //
CREATE PROCEDURE sp_costStatisticsByDYM()
BEGIN

    -- Cost by Day
    SELECT DATE(Bill.Created)                            AS Date,
           SUM(Bill_Detail.Price * Bill_Detail.Quantity) AS TotalCost
    FROM Bill_Detail
             JOIN Bill ON Bill.Bill_id = Bill_Detail.Bill_Id
    WHERE Bill.Bill_Type = false -- 1 Nhập
    GROUP BY DATE(Bill.Created);

    -- Cost by mont
    SELECT DATE_FORMAT(Bill.Created, '%Y-%m')            AS Month,
           SUM(Bill_Detail.Price * Bill_Detail.Quantity) AS TotalCost
    FROM Bill_Detail
             JOIN Bill ON Bill.Bill_id = Bill_Detail.Bill_Id
    WHERE Bill.Bill_Type = 0-- 1 Nhập
    GROUP BY DATE_FORMAT(Bill.Created, '%Y-%m');

    -- Cost by year
    SELECT YEAR(Bill.Created)                            AS Year,
           SUM(Bill_Detail.Price * Bill_Detail.Quantity) AS TotalCost
    FROM Bill_Detail
             JOIN Bill ON Bill.Bill_id = Bill_Detail.Bill_Id
    WHERE Bill.Bill_Type = 0 -- 1 Nhập
    GROUP BY YEAR(Bill.Created);
END //

DELIMITER ;

-- thống kê cost theo range
call sp_CostStatistics_ByRange('2023-10-11', '2023-10-09');
drop procedure sp_CostStatistics_ByRange;
DELIMITER //
CREATE PROCEDURE sp_CostStatistics_ByRange(IN StartDate DATE, IN EndDate DATE)
BEGIN
    IF StartDate > EndDate THEN
        SET @tempDate = StartDate;
        SET StartDate = EndDate;
        SET EndDate = @tempDate;
    END IF;
    SELECT SUM(Price * Quantity) AS TotalCost
    FROM Bill_Detail
             JOIN Bill ON Bill.Bill_id = Bill_Detail.Bill_Id
    WHERE DATE(Bill.Created) BETWEEN StartDate AND EndDate
      AND Bill.Bill_Type = 0;
END;

DELIMITER ;

-- thống kê profit by D M Y
drop procedure sp_profitStatisticsByDYM;
call sp_profitStatisticsByDYM;
DELIMITER //

CREATE PROCEDURE sp_profitStatisticsByDYM()
BEGIN

    -- Profit by Day
    SELECT Date,
           (Revenue - Cost) AS TotalProfit
    FROM (SELECT DATE(Bill.Created)                                                       AS Date,
                 SUM(IF(Bill.Bill_Type = 1, Bill_Detail.Price * Bill_Detail.Quantity, 0)) AS Revenue,
                 SUM(IF(Bill.Bill_Type = 0, Bill_Detail.Price * Bill_Detail.Quantity, 0)) AS Cost
          FROM Bill_Detail
                   JOIN
               Bill ON Bill.Bill_id = Bill_Detail.Bill_Id
          GROUP BY DATE(Bill.Created)) AS DailyProfit;

    -- Profit by Month
    SELECT Month,
           (Revenue - Cost) AS TotalProfit
    FROM (SELECT DATE_FORMAT(Bill.Created, '%Y-%m')                                       AS Month,
                 SUM(IF(Bill.Bill_Type = 1, Bill_Detail.Price * Bill_Detail.Quantity, 0)) AS Revenue,
                 SUM(IF(Bill.Bill_Type = 0, Bill_Detail.Price * Bill_Detail.Quantity, 0)) AS Cost
          FROM Bill_Detail
                   JOIN
               Bill ON Bill.Bill_id = Bill_Detail.Bill_Id
          GROUP BY DATE_FORMAT(Bill.Created, '%Y-%m')) AS MonthlyProfit;

    -- Profit by Year
    SELECT Year,
           (Revenue - Cost) AS TotalProfit
    FROM (SELECT YEAR(Bill.Created)                                                       AS Year,
                 SUM(IF(Bill.Bill_Type = 1, Bill_Detail.Price * Bill_Detail.Quantity, 0)) AS Revenue,
                 SUM(IF(Bill.Bill_Type = 0, Bill_Detail.Price * Bill_Detail.Quantity, 0)) AS Cost
          FROM Bill_Detail
                   JOIN
               Bill ON Bill.Bill_id = Bill_Detail.Bill_Id
          GROUP BY YEAR(Bill.Created)) AS YearlyProfit;

END //

DELIMITER ;

-- thống kê profit by range
call sp_ProfitStatistics_ByRange('2023-09-09', '2023-12-12');
drop procedure sp_ProfitStatistics_ByRange;
DELIMITER //
CREATE PROCEDURE sp_ProfitStatistics_ByRange(IN StartDate DATE, IN EndDate DATE)
BEGIN
    IF StartDate > EndDate THEN
        SET @tempDate = StartDate;
        SET StartDate = EndDate;
        SET EndDate = @tempDate;
    END IF;
    SELECT (Revenue - Cost) AS TotalProfit
    FROM (SELECT SUM(IF(Bill.Bill_Type = 1, Bill_Detail.Price * Bill_Detail.Quantity, 0)) AS Revenue,
                 SUM(IF(Bill.Bill_Type = 0, Bill_Detail.Price * Bill_Detail.Quantity, 0)) AS Cost
          FROM Bill_Detail
                   JOIN
               Bill ON Bill.Bill_id = Bill_Detail.Bill_Id
          WHERE DATE(Bill.Created) BETWEEN StartDate AND EndDate) AS DailyProfit;
END;

DELIMITER ;

DELIMITER //
drop procedure sp_EmployeeStatusStatistics;
CREATE PROCEDURE sp_EmployeeStatusStatistics(out ActiveEmp int, out TemporaryLeave int, out QuitJob int)
BEGIN
    SELECT COUNT(*)
    into ActiveEmp
    FROM Employee
    Where Emp_Status = 0
    GROUP BY Emp_Status = 0;

    SELECT COUNT(*)
    into TemporaryLeave
    FROM Employee
    Where Emp_Status = 1
    GROUP BY Emp_Status = 1;

    SELECT COUNT(*)
    into QuitJob
    FROM Employee
    Where Emp_Status = 2
    GROUP BY Emp_Status = 2;
END //

DELIMITER ;
call sp_EmployeeStatusStatistics;

call sp_ProductStatistics_Asc('2023-10-09', '2023-10-11', false);
drop procedure sp_ProductStatistics_Asc;
DELIMITER //
CREATE PROCEDURE sp_ProductStatistics_Asc(
    IN StartDate DATE,
    IN EndDate DATE,
    IN BillType BIT
)
BEGIN
    -- Đảm bảo rằng StartDate luôn trước hoặc bằng EndDate
    IF StartDate > EndDate THEN
        SET @tempDate = StartDate;
        SET StartDate = EndDate;
        SET EndDate = @tempDate;
    END IF;

    SELECT Product.Product_Name, SUM(Bill_Detail.Quantity) AS TotalAmount
    FROM Bill_Detail
             JOIN Bill ON Bill.Bill_id = Bill_Detail.Bill_Id
             JOIN Product ON Product.Product_Id = Bill_Detail.Product_Id
    WHERE DATE(Bill.Created) BETWEEN StartDate AND EndDate
      AND Bill_Type = BillType
    GROUP BY Product.Product_Id
    ORDER BY TotalAmount;
END//
DELIMITER ;

call sp_ProductStatistics_Desc('2023-10-09', '2023-10-14', false);
drop procedure sp_ProductStatistics_Desc;
DELIMITER //
CREATE PROCEDURE sp_ProductStatistics_Desc(
    IN StartDate DATE,
    IN EndDate DATE,
    IN BillType BIT
)
BEGIN
    -- Đảm bảo rằng StartDate luôn trước hoặc bằng EndDate
    IF StartDate > EndDate THEN
        SET @tempDate = StartDate;
        SET StartDate = EndDate;
        SET EndDate = @tempDate;
    END IF;

    SELECT Product.Product_Name, SUM(Bill_Detail.Quantity) AS TotalAmount
    FROM Bill_Detail
             JOIN Bill ON Bill.Bill_id = Bill_Detail.Bill_Id
             JOIN Product ON Product.Product_Id = Bill_Detail.Product_Id
    WHERE DATE(Bill.Created) BETWEEN StartDate AND EndDate
      AND Bill_Type = BillType
    GROUP BY Product.Product_Id
    ORDER BY TotalAmount DESC;
END//
DELIMITER ;


-- <><><><><><> Login menu <><><><><><>

DELIMITER //
CREATE PROCEDURE checkExistsUser(IN userName VARCHAR(30), out userExits boolean)
BEGIN
    select exists(select 1
                  from Account
                  where User_name = userName)
    into userExits;
end //
DELIMITER ;

DELIMITER //
create procedure checkValidAccount(in userName varchar(30), in Password_Input varchar(30), out isValid boolean)
begin
    select exists(select 1 from Account where User_name = userName and Password = Password_Input)
    into isValid;
end //
DELIMITER ;

-- check isBlockAcc
DELIMITER //
CREATE PROCEDURE checkIsBlock(
    in p_emId CHAR(5),
    OUT isValid bit
)
BEGIN
    SELECT Acc_status
    into isValid
    FROM Account
    WHERE Emp_id = p_emId;
END //
DELIMITER ;


-- check permission
drop procedure checkAccountPermission;
DELIMITER //
CREATE PROCEDURE checkAccountPermission(
    IN userName VARCHAR(30),
    OUT role boolean
)
BEGIN
    SELECT Permission
    INTO role
    FROM Account
    WHERE User_name = userName;
END //
DELIMITER ;

-- get emp_id
drop procedure GetEmpId;
DELIMITER //
CREATE PROCEDURE GetEmpId(IN userName VARCHAR(30))
BEGIN
    SELECT Emp_id
    FROM Account
    WHERE User_name = userName
    LIMIT 1;
END //
DELIMITER ;
call GetEmpId('admin');

-- <><><><><><> User Authorized <><><><><><>
drop procedure checkEmpIsAuthorizeToBill;
call checkEmpIsAuthorizeToBill(null, 'billcode23', '1', @outtest);
select @outtest;
DELIMITER //
CREATE Procedure checkEmpIsAuthorizeToBill(p_bill_id BIGINT, p_bill_Code varchar(10), p_emp_id char(5),
                                           OUT valid boolean)
begin
    set valid = false;
    select exists(select 1
                  from Bill b
                  where Emp_id_created = p_emp_id
                    and (b.Bill_id = p_bill_id or p_bill_Code = b.Bill_Code))
    into valid;
end //
DELIMITER ;


select *
from Product p
order by p.Quantity desc limit 5;