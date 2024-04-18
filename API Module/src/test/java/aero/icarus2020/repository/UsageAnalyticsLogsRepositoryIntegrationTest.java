package aero.icarus2020.repository;
import aero.icarus2020.models.UsageAnalyticsLogsModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.Optional;

/** This test class will run only if the with the command "-Dtest-groups=integration-tests"
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@IfProfileValue(name = "test-groups", values = {"integration-tests"})
public class UsageAnalyticsLogsRepositoryIntegrationTest {

    private UsageAnalyticsLogsModel usageAnalyticsLogsModel;

    @Autowired
    private UsageAnalyticsLogsRepository usageAnalyticsLogsRepository;

    private long id;

    /**
     * This method creates a fake usageAnalyticsLogsModel before executing any tests
     */
    @Before
    public void setup() {
        usageAnalyticsLogsModel = new UsageAnalyticsLogsModel("USER_CONNECT", "{}", new Date());
    }

    /**
     * This method checks that it will return either a EmptyResultDataAccessException exception either an empty
     * response since the usage analytic log with id "-1" does not exist.
     */
    @Test
    public void findByIdNotValidTest() {
        try {
            Optional<UsageAnalyticsLogsModel> foundLog = usageAnalyticsLogsRepository.findById((long) -1);
            assertThat(foundLog.isPresent()).isEqualTo(false);
        } catch (EmptyResultDataAccessException ignored) {
        }
    }

    /**
     * This method saves the testing usageAnalyticsLogsModel and tests that it was indeed saved.
     */
    @Test
    public void findByIdValidTest() {
        UsageAnalyticsLogsModel ug = usageAnalyticsLogsRepository.save(usageAnalyticsLogsModel);

        assertNotNull(ug);
        Optional<UsageAnalyticsLogsModel> foundUsageAnalytics = usageAnalyticsLogsRepository.findById(ug.getId());

        assertThat(foundUsageAnalytics.isPresent()).isEqualTo(true);

        this.id = ug.getId();
    }

    /**
     * This method deletes the testing usageAnalyticsLogsModel before exiting
     */
    @After
    public void cleanUp() {
        try {
            usageAnalyticsLogsRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ignored) {
        }
    }
}
