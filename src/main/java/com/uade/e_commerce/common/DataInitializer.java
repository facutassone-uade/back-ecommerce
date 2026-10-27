package com.uade.e_commerce.common;

import java.util.Arrays;
import java.util.List;

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
        Category hogar = new Category();
        hogar.setName("Hogar");

        Category cocina = new Category();
        cocina.setName("Cocina");

        Category banio = new Category();
        banio.setName("Baño");

        categoryRepository.saveAll(Arrays.asList(hogar, cocina, banio));
    }

    private void initializeProducts() {
        List<Category> categories = categoryRepository.findAll();
        Category hogar = categories.stream().filter(category -> "Hogar".equals(category.getName())).findFirst().orElse(null);
        Category cocina = categories.stream().filter(category -> "Cocina".equals(category.getName())).findFirst().orElse(null);
        Category banio = categories.stream().filter(category -> "Baño".equals(category.getName())).findFirst().orElse(null);

        Product jarron = new Product();
        jarron.setName("Jarrón decorativo");
        jarron.setDescription("Jarrón de cerámica con diseño artesanal para decorar cualquier espacio");
        jarron.setPrice(1850.00);
        jarron.setStock(12);
        jarron.setImageUrl("https://images.pexels.com/photos/28991198/pexels-photo-28991198.jpeg");
        jarron.setCategories(List.of(hogar));

        Product vela = new Product();
        vela.setName("Vela aromática");
        vela.setDescription("Vela con aroma suave para crear un ambiente cálido y relajante");
        vela.setPrice(1200.00);
        vela.setStock(18);
        vela.setImageUrl("https://images.pexels.com/photos/29737854/pexels-photo-29737854.jpeg");
        vela.setCategories(List.of(hogar));

        Product cuadro = new Product();
        cuadro.setName("Cuadro decorativo de madera");
        cuadro.setDescription("Cuadro rústico de madera para aportar estilo y calidez a la pared");
        cuadro.setPrice(2100.00);
        cuadro.setStock(8);
        cuadro.setImageUrl("https://images.pexels.com/photos/6618329/pexels-photo-6618329.jpeg");
        cuadro.setCategories(List.of(hogar));

        Product cuencos = new Product();
        cuencos.setName("Set de cuencos de cerámica");
        cuencos.setDescription("Set de cuencos de cerámica en tonos neutros para cocina y servicio diario");
        cuencos.setPrice(3200.00);
        cuencos.setStock(10);
        cuencos.setImageUrl("https://images.pexels.com/photos/14320922/pexels-photo-14320922.jpeg");
        cuencos.setCategories(List.of(cocina));

        Product frasco = new Product();
        frasco.setName("Frasco de almacenamiento");
        frasco.setDescription("Frasco hermético para guardar cereales, harinas o legumbres con orden");
        frasco.setPrice(1450.00);
        frasco.setStock(20);
        frasco.setImageUrl("https://images.pexels.com/photos/7263025/pexels-photo-7263025.jpeg");
        frasco.setCategories(List.of(cocina));

        Product tabla = new Product();
        tabla.setName("Tabla de corte de madera");
        tabla.setDescription("Tabla resistente de madera ideal para preparar alimentos y decorar la cocina");
        tabla.setPrice(2600.00);
        tabla.setStock(14);
        tabla.setImageUrl("https://images.pexels.com/photos/5964663/pexels-photo-5964663.jpeg");
        tabla.setCategories(List.of(cocina));

        Product toallas = new Product();
        toallas.setName("Juego de toallas de algodón");
        toallas.setDescription("Toallas suaves y absorbentes para un baño cómodo y elegante");
        toallas.setPrice(2900.00);
        toallas.setStock(15);
        toallas.setImageUrl("https://images.pexels.com/photos/7691101/pexels-photo-7691101.jpeg");
        toallas.setCategories(List.of(banio));

        Product dispensador = new Product();
        dispensador.setName("Dispensador de jabón");
        dispensador.setDescription("Dispensador práctico para baño con diseño moderno y funcional");
        dispensador.setPrice(2400.00);
        dispensador.setStock(9);
        dispensador.setImageUrl("https://images.pexels.com/photos/4008464/pexels-photo-4008464.jpeg");
        dispensador.setCategories(List.of(banio));

        Product canasto = new Product();
        canasto.setName("Canasto organizador");
        canasto.setDescription("Canasto de almacenamiento para ordenar toallas, productos y accesorios del baño");
        canasto.setPrice(1750.00);
        canasto.setStock(11);
        canasto.setImageUrl("https://images.pexels.com/photos/31390662/pexels-photo-31390662.jpeg");
        canasto.setCategories(List.of(banio));

        productRepository.saveAll(Arrays.asList(jarron, vela, cuadro, cuencos, frasco, tabla, toallas, dispensador, canasto));
    }
}
