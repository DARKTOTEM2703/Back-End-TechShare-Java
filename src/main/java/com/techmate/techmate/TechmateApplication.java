package com.techmate.techmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import com.techmate.techmate.infrastructure.config.AppProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = { "com.techmate.techmate" })
@EnableConfigurationProperties(AppProperties.class)
public class TechmateApplication {

	public static void main(String[] args) {
		// Cargar .env (si existe) antes de inicializar Spring para que variables como
		// JWT_SECRET
		// estén disponibles como System properties y puedan inyectarse con @Value.
		loadDotEnv();

		SpringApplication.run(TechmateApplication.class, args);
	}

	private static void loadDotEnv() {
		try {
			Path envPath = Path.of(".env");
			if (!Files.exists(envPath)) {
				return; // no hay .env local
			}

			List<String> lines = Files.readAllLines(envPath, StandardCharsets.UTF_8);
			for (String raw : lines) {
				String line = raw.trim();
				if (line.isEmpty() || line.startsWith("#"))
					continue;

				int idx = line.indexOf('=');
				if (idx <= 0)
					continue;
				String key = line.substring(0, idx).trim();
				String value = line.substring(idx + 1).trim();

				// Remove optional surrounding quotes
				if ((value.startsWith("\"") && value.endsWith("\""))
						|| (value.startsWith("'") && value.endsWith("'"))) {
					value = value.substring(1, value.length() - 1);
				}

				// No sobrescribir variables ya definidas en el sistema/entorno
				String existingSysProp = System.getProperty(key);
				String existingEnv = System.getenv(key);
				if ((existingSysProp == null || existingSysProp.isBlank())
						&& (existingEnv == null || existingEnv.isBlank())) {
					System.setProperty(key, value);
					// también dejamos en el cerrr para debug durante desarrollo
					System.out.println("[dotenv] cargado " + key + " desde .env");
				}
			}
		} catch (Exception e) {
			System.err.println("[dotenv] No se pudo cargar .env: " + e.getMessage());
		}
	}

}
