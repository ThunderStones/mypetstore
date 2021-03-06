package org.csu.mypetstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("org.csu.mypetstore.persistence")
public class MypetstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MypetstoreApplication.class, args);
    }

}
