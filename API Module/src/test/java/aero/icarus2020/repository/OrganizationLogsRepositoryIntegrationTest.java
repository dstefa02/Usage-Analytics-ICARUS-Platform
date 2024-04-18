package aero.icarus2020.repository;
import aero.icarus2020.models.OrganizationLogsModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;
import java.util.List;
/** This test class will run only if the with the command "-Dtest-groups=integration-tests"
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@IfProfileValue(name = "test-groups", values = {"integration-tests"})
public class OrganizationLogsRepositoryIntegrationTest {

    private OrganizationLogsModel organizationLogsModel;

    @Autowired
    private OrganizationLogsRepository organizationLogsRepository;

    /**
     * This method creates a fake organizationLogsModel before executing any tests
     */
    @Before
    public void setup() {
        organizationLogsModel = new OrganizationLogsModel((long)13, (long)3);
    }

    /**
     * This method checks that it will return either a EmptyResultDataAccessException exception either an empty
     * response since the log with organization id "-1" does not exist.
     */
    @Test
    public void findByIdNotValidTest() {
        try {
            List<Object[]> foundLog = organizationLogsRepository.findByOrganizationId((long) -1);
            assertEquals(foundLog.size(), 0);
        } catch (Exception ignored) {
        }
    }

    /**
     * This method saves the testing organizationLogsModel and tests that it was indeed saved.
     */
    @Test
    public void findByIdValidTest() {
        OrganizationLogsModel olm = organizationLogsRepository.save(organizationLogsModel);

        assertNotNull(olm);
        List<Object[]> foundAssetLogs = organizationLogsRepository.findByOrganizationId(olm.getOrganizationId());

        assertNotNull(foundAssetLogs);
    }

    /**
     * This method deletes the testing organizationLogsModel before exiting
     */
    @After
    public void cleanUp() {
        try {
            organizationLogsRepository.deleteLog(13, 3);
        } catch (Exception ignored) {
        }
    }
}
