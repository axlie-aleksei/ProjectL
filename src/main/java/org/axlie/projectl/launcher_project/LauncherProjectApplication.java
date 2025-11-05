package org.axlie.projectl.launcher_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.content.fs.config.EnableFilesystemStores;

@SpringBootApplication
@EnableFilesystemStores
public class LauncherProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(LauncherProjectApplication.class, args);
    }

}
