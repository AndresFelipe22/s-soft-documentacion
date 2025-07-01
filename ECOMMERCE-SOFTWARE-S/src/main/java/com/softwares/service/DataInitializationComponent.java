package com.softwares.service;


import com.softwares.domain.USER_ROLE;
import com.softwares.models.BusinessDetails;
import com.softwares.models.Seller;
import com.softwares.models.User;
import com.softwares.repository.SellerRepository;
import com.softwares.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SellerRepository sellerRepository;



    @Override
    public void run(String... args) {
        initializeAdminUser();
        initializeSellerUser();
    }

    private void initializeAdminUser() {
        String adminUsername = "andresfmaceto@gmail.com";

        if (userRepository.findByEmail(adminUsername)==null) {
            User adminUser = new User();

            adminUser.setPassword(passwordEncoder.encode("mateo1234"));
            adminUser.setFullName("Andres");
            adminUser.setEmail(adminUsername);
            adminUser.setRole(USER_ROLE.ROLE_ADMIN);

            User admin=userRepository.save(adminUser);
        }
    }




    private void initializeSellerUser() {
        String sellerUsername = "andresfmaceto@gmail.com";

        if (sellerRepository.findByEmail(sellerUsername)==null) {
            Seller sellerUser = new Seller();

            sellerUser.setPassword(passwordEncoder.encode("seller1234"));
            sellerUser.setSellerName("Selle");
            sellerUser.setEmail(sellerUsername);
            sellerUser.setRole(USER_ROLE.ROLE_SELLER);


            BusinessDetails businessDetails = new BusinessDetails();
            businessDetails.setBusinessName("MATECNO");
            sellerUser.setBusinessDetails(businessDetails);


            Seller seller= sellerRepository.save(sellerUser);

        }
    }


}