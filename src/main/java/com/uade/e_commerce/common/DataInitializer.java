package com.uade.e_commerce.common;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.uade.e_commerce.category.Category;
import com.uade.e_commerce.category.CategoryRepository;
import com.uade.e_commerce.customer.Address;
import com.uade.e_commerce.customer.Customer;
import com.uade.e_commerce.customer.CustomerRepository;
import com.uade.e_commerce.product.Product;
import com.uade.e_commerce.product.ProductRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository,
            CustomerRepository customerRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no data exists
        if (categoryRepository.count() == 0) {
            initializeCategories();
            initializeProducts();
            initializeCustomers();
            System.out.println("✓ Datos de prueba cargados exitosamente");
        }
    }

    private void initializeCategories() {
        Category electronica = new Category();
        electronica.setName("Electrónica");

        Category ropa = new Category();
        ropa.setName("Ropa");

        Category libros = new Category();
        libros.setName("Libros");

        Category hogar = new Category();
        hogar.setName("Hogar");

        categoryRepository.saveAll(Arrays.asList(electronica, ropa, libros, hogar));
        System.out.println("✓ Categorías insertadas");
    }

    private void initializeProducts() {
        List<Category> electronicaList = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equals("Electrónica")).toList();
        List<Category> ropaList = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equals("Ropa")).toList();
        List<Category> librosList = categoryRepository.findAll().stream()
                .filter(c -> c.getName().equals("Libros")).toList();

        // Productos Electrónica
        Product laptop = new Product();
        laptop.setName("Laptop Dell");
        laptop.setDescription("Laptop de 15 pulgadas con procesador Intel i7");
        laptop.setPrice(1200.00);
        laptop.setStock(10);
        laptop.setCategories(electronicaList);

        Product mouse = new Product();
        mouse.setName("Mouse Logitech");
        mouse.setDescription("Mouse inalámbrico con sensor óptico");
        mouse.setPrice(35.00);
        mouse.setStock(50);
        mouse.setCategories(electronicaList);

        Product teclado = new Product();
        teclado.setName("Teclado Mecánico");
        teclado.setDescription("Teclado mecánico RGB con switches Cherry MX");
        teclado.setPrice(150.00);
        teclado.setStock(25);
        teclado.setCategories(electronicaList);

        // Productos Ropa
        Product camiseta = new Product();
        camiseta.setName("Camiseta Básica");
        camiseta.setDescription("Camiseta de algodón 100% - Disponible en varios colores");
        camiseta.setPrice(25.00);
        camiseta.setStock(100);
        camiseta.setCategories(ropaList);

        Product pantalon = new Product();
        pantalon.setName("Pantalón Jeans");
        pantalon.setDescription("Pantalón jeans azul oscuro - Talla única");
        pantalon.setPrice(65.00);
        pantalon.setStock(40);
        pantalon.setCategories(ropaList);

        Product zapatillas = new Product();
        zapatillas.setName("Zapatillas Deportivas");
        zapatillas.setDescription("Zapatillas de running con amortiguación");
        zapatillas.setPrice(95.00);
        zapatillas.setStock(30);
        zapatillas.setCategories(ropaList);

        // Productos Libros
        Product libroSpring = new Product();
        libroSpring.setName("Spring en Acción");
        libroSpring.setDescription("Guía completa de Spring Framework");
        libroSpring.setPrice(45.00);
        libroSpring.setStock(20);
        libroSpring.setCategories(librosList);

        Product libroJava = new Product();
        libroJava.setName("Java Efectivo");
        libroJava.setDescription("Mejores prácticas en Java moderno");
        libroJava.setPrice(50.00);
        libroJava.setStock(15);
        libroJava.setCategories(librosList);

        productRepository.saveAll(Arrays.asList(
                laptop, mouse, teclado,
                camiseta, pantalon, zapatillas,
                libroSpring, libroJava
        ));
        System.out.println("✓ Productos insertados");
    }

    private void initializeCustomers() {
        Address address1 = new Address();
        address1.setStreet("Av. Principal 123");
        address1.setCity("Buenos Aires");
        address1.setZipCode("1425");
        address1.setCountry("Argentina");

        Customer customer1 = new Customer();
        customer1.setName("Juan");
        customer1.setLastName("García");
        customer1.setNationalId(35123456L);
        customer1.setEmail("juan.garcia@email.com");
        customer1.setPhone(1145678901L);
        customer1.setAddress(address1);
        customer1.setUsername("juan_garcia");
        customer1.setPassword("password123");

        Address address2 = new Address();
        address2.setStreet("Calle Secundaria 456");
        address2.setCity("CABA");
        address2.setZipCode("1015");
        address2.setCountry("Argentina");

        Customer customer2 = new Customer();
        customer2.setName("María");
        customer2.setLastName("López");
        customer2.setNationalId(36789012L);
        customer2.setEmail("maria.lopez@email.com");
        customer2.setPhone(1187654321L);
        customer2.setAddress(address2);
        customer2.setUsername("maria_lopez");
        customer2.setPassword("password456");

        Address address3 = new Address();
        address3.setStreet("Calle Tercera 789");
        address3.setCity("La Plata");
        address3.setZipCode("1900");
        address3.setCountry("Argentina");

        Customer customer3 = new Customer();
        customer3.setName("Carlos");
        customer3.setLastName("Rodríguez");
        customer3.setNationalId(37456789L);
        customer3.setEmail("carlos.rodriguez@email.com");
        customer3.setPhone(1199876543L);
        customer3.setAddress(address3);
        customer3.setUsername("carlos_rodriguez");
        customer3.setPassword("password789");

        customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
        System.out.println("✓ Clientes insertados");
    }
}

