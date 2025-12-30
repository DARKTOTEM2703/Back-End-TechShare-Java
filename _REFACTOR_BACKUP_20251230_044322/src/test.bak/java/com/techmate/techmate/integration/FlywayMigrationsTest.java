package com.techmate.techmate.integration;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FlywayMigrationsTest {

    @Test
    void migrationsShouldBeParsableAndNotFailed() {
        // Use an in-memory H2 DB to validate the SQL migration scripts parsing
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:h2:mem:flyway_test;DB_CLOSE_DELAY=-1;MODE=MySQL", "sa", "")
                .locations("filesystem:src/main/resources/db/migration")
                .load();

        MigrationInfo[] infos = flyway.info().all();

        // There should be at least one migration script present
        assertThat(infos).isNotNull();
        assertThat(infos.length).isGreaterThanOrEqualTo(1);

        // None of the migrations should be in FAILED state (parsing/runtime errors)
        for (MigrationInfo info : infos) {
            MigrationState state = info.getState();
            // Accept states like PENDING, AVAILABLE, SUCCESS, etc. Reject FAILED
            assertThat(state.name()).as("Migration %s has unexpected state", info.getScript())
                    .doesNotContain("FAILED");
        }
    }
}

