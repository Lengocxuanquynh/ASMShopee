package com.fpoly.java5demo.components;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fpoly.java5demo.entities.Category;
import com.fpoly.java5demo.entities.Product;
import com.fpoly.java5demo.entities.User;
import com.fpoly.java5demo.jpas.CategoryJPA;
import com.fpoly.java5demo.jpas.ProductJPA;
import com.fpoly.java5demo.jpas.UserJPA;

@Component
public class DatabaseDataInitializer implements CommandLineRunner {

	@Autowired
	private UserJPA userJPA;

	@Autowired
	private CategoryJPA categoryJPA;

	@Autowired
	private ProductJPA productJPA;

	@Override
	public void run(String... args) throws Exception {
		// 1. Khởi tạo tài khoản mẫu
		if (userJPA.count() == 0) {
			User admin = new User();
			admin.setUsername("admin");
			admin.setPassword("123456");
			admin.setName("Quản Trị Viên Shopee");
			admin.setEmail("admin@shopee.vn");
			admin.setRole(1); // 1: Admin
			admin.setStatus(true);
			userJPA.save(admin);

			User user = new User();
			user.setUsername("user1");
			user.setPassword("123456");
			user.setName("Nguyễn Văn A");
			user.setEmail("nguyenvana@gmail.com");
			user.setRole(0); // 0: User
			user.setStatus(true);
			userJPA.save(user);

			System.out.println(">>> Đã khởi tạo tài khoản mẫu: admin/123456 và user1/123456");
		}

		// 2. Khởi tạo danh mục sản phẩm
		if (categoryJPA.count() == 0) {
			Category cat1 = new Category();
			cat1.setName("Điện Thoại & Phụ Kiện");

			Category cat2 = new Category();
			cat2.setName("Máy Tính & Laptop");

			Category cat3 = new Category();
			cat3.setName("Thời Trang Nam");

			Category cat4 = new Category();
			cat4.setName("Thời Trang Nữ");

			Category cat5 = new Category();
			cat5.setName("Thiết Bị Điện Tử");

			List<Category> savedCategories = categoryJPA.saveAll(Arrays.asList(cat1, cat2, cat3, cat4, cat5));
			System.out.println(">>> Đã khởi tạo 5 danh mục sản phẩm Shopee");

			// 3. Khởi tạo sản phẩm mẫu
			if (productJPA.count() == 0) {
				Category c1 = savedCategories.get(0); // Điện thoại
				Category c2 = savedCategories.get(1); // Laptop
				Category c3 = savedCategories.get(2); // Nam
				Category c4 = savedCategories.get(3); // Nữ
				Category c5 = savedCategories.get(4); // Điện tử

				List<Product> sampleProducts = Arrays.asList(
					new Product(0, "Điện thoại Apple iPhone 15 Pro Max 256GB Titan Tự Nhiên", 29490000, 35, true, c1, null, null),
					new Product(0, "Tai nghe không dây Bluetooth True Wireless chống ồn chủ động", 350000, 150, true, c1, null, null),
					new Product(0, "Củ sạc nhanh 20W Type-C chuẩn PD cho iPhone / iPad", 180000, 200, true, c1, null, null),
					new Product(0, "Laptop Apple MacBook Air M2 13.6 inch 8GB RAM 256GB SSD", 23990000, 15, true, c2, null, null),
					new Product(0, "Chuột máy tính không dây công thái học Silent Click", 220000, 80, true, c2, null, null),
					new Product(0, "Bàn phím cơ không dây Bluetooth RGB Led 87 phím Hot-swap", 750000, 50, true, c2, null, null),
					new Product(0, "Áo thun nam ngắn tay cổ tròn cotton co giãn 4 chiều thoáng mát", 99000, 300, true, c3, null, null),
					new Product(0, "Quần short đùi nam thể thao vải dù gió chống thấm nước", 85000, 180, true, c3, null, null),
					new Product(0, "Áo khoác dù bomber 2 lớp phong cách trẻ trung Ulzzang", 219000, 95, true, c3, null, null),
					new Product(0, "Váy hoa nhí dáng xòe tiểu thư vintage mùa hè cổ vuông", 165000, 110, true, c4, null, null),
					new Product(0, "Set áo sơ mi croptop tay bồng kèm chân váy chữ A xếp ly", 240000, 75, true, c4, null, null),
					new Product(0, "Loa Bluetooth không dây mini âm bass siêu trầm chống nước", 290000, 120, true, c5, null, null),
					new Product(0, "Đồng hồ thông minh Smartwatch theo dõi nhịp tim chống nước", 680000, 60, true, c5, null, null)
				);

				productJPA.saveAll(sampleProducts);
				System.out.println(">>> Đã khởi tạo 13 sản phẩm mẫu phong cách Shopee");
			}
		}
	}
}
