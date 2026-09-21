package com.uade.e_commerce.common;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.uade.e_commerce.auth.Role;
import com.uade.e_commerce.auth.User;
import com.uade.e_commerce.auth.UserRepository;
import com.uade.e_commerce.category.Category;
import com.uade.e_commerce.category.CategoryRepository;
import com.uade.e_commerce.customer.Customer;
import com.uade.e_commerce.customer.CustomerRepository;
import com.uade.e_commerce.product.Product;
import com.uade.e_commerce.product.ProductRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository,
            UserRepository userRepository, CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            initializeAdmin();
            initializeCustomers();
        }

        if (categoryRepository.count() == 0) {
            initializeCategories();
            initializeProducts();
            System.out.println("✓ Test data loaded");
        }
    }

    private void initializeAdmin() {
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("System");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);
        User savedAdmin = userRepository.save(admin);

        Customer adminCustomer = new Customer();
        adminCustomer.setUser(savedAdmin);
        adminCustomer.setNationalId(30123456L);
        adminCustomer.setPhone(1112345678L);
        customerRepository.save(adminCustomer);

        System.out.println("✓ Admin created: admin@example.com");
    }

    private void initializeCustomers() {
        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Garcia");
        user1.setEmail("juan.garcia@email.com");
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setRole(Role.USER);
        User savedUser1 = userRepository.save(user1);

        Customer customer1 = new Customer();
        customer1.setUser(savedUser1);
        customer1.setNationalId(35123456L);
        customer1.setPhone(1145678901L);
        customerRepository.save(customer1);

        User user2 = new User();
        user2.setFirstName("Mary");
        user2.setLastName("Lopez");
        user2.setEmail("maria.lopez@email.com");
        user2.setPassword(passwordEncoder.encode("password456"));
        user2.setRole(Role.USER);
        User savedUser2 = userRepository.save(user2);

        Customer customer2 = new Customer();
        customer2.setUser(savedUser2);
        customer2.setNationalId(36789012L);
        customer2.setPhone(1187654321L);
        customerRepository.save(customer2);

        System.out.println("✓ Test customers created");
    }

    private void initializeCategories() {
        Category electronics = new Category();
        electronics.setName("Electronics");

        Category clothes = new Category();
        clothes.setName("Clothes");

        Category books = new Category();
        books.setName("Books");

        categoryRepository.saveAll(Arrays.asList(electronics, clothes, books));
    }

    private void initializeProducts() {
        var categories = categoryRepository.findAll();

        Product laptop = new Product();
        laptop.setName("Dell Laptop");
        laptop.setDescription("15 inch laptop");
        laptop.setPrice(1200.00);
        laptop.setStock(10);
        laptop.setCategories(categories);

        Product mouse = new Product();
        mouse.setName("Logitech Mouse");
        mouse.setDescription("Wireless mouse");
        mouse.setPrice(35.00);
        mouse.setStock(50);
        mouse.setCategories(categories);

        Product shirt = new Product();
        shirt.setName("Basic T-Shirt");
        shirt.setDescription("Cotton t-shirt");
        shirt.setPrice(25.00);
        shirt.setStock(100);
        shirt.setCategories(categories);

        productRepository.saveAll(Arrays.asList(laptop, mouse, shirt));
    }
}
