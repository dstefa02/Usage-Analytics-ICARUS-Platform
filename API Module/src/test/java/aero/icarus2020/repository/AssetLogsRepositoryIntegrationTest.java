package aero.icarus2020.repository;
import aero.icarus2020.models.AssetLogsModel;
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
public class AssetLogsRepositoryIntegrationTest {

    private AssetLogsModel assetLogsModel;

    @Autowired
    private AssetLogsRepository assetLogsRepository;

    /**
     * This method creates a fake assetLogsModel before executing any tests
     */
    @Before
    public void setup() {
        assetLogsModel = new AssetLogsModel((long)86, (long)3, 'd');
    }

    /**
     * This method checks that it will return either a EmptyResultDataAccessException exception either an empty
     * response since the log with asset id "-1" does not exist.
     */
    @Test
    public void findByIdNotValidTest() {
        try {
            List<Object[]> foundLog = assetLogsRepository.findByAssetId((long) -1);
            assertEquals(foundLog.size(), 0);
        } catch (Exception ignored) {
        }
    }

    /**
     * This method saves the testing assetLogsModel and tests that it was indeed saved.
     */
    @Test
    public void findByIdValidTest() {
        AssetLogsModel alm = assetLogsRepository.save(assetLogsModel);

        assertNotNull(alm);
        List<Object[]> foundAssetLogs = assetLogsRepository.findByAssetId(alm.getAssetId());

        assertNotNull(foundAssetLogs);
    }

    /**
     * This method deletes the testing assetLogsModel before exiting
     */
    @After
    public void cleanUp() {
        try {
            assetLogsRepository.deleteLog(86, 3);
        } catch (Exception ignored) {
        }
    }
}
