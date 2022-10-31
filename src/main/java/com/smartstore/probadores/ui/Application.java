package com.smartstore.probadores.ui;

import com.smartstore.probadores.ui.views.MainLayout;
import com.smartstore.probadores.ui.views.about.StartupView;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication(scanBasePackageClasses = { MainLayout.class, StartupView.class, Application.class }, exclude = ErrorMvcAutoConfiguration.class)
@Theme(value = "probadores")
@PWA(name = "Probadores", shortName = "Probadores", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application  extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
