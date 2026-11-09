package com.akiba.artisan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ArtisanServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
