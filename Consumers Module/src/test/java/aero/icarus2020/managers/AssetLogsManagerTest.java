package aero.icarus2020.managers;

import aero.icarus2020.models.AssetLogsModel;
import aero.icarus2020.repositories.AssetLogsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class  AssetLogsManagerTest {

    @MockBean
    private AssetLogsRepository assetLogsRepository;

    @Autowired
    private AssetLogsManager assetLogsManager;

    /**
     * This test method checks that the storeLogs method will create and store correctly the log.
     */
    @Test
    public void storeLogsTest() {
        // mock assetLogsRepository
        when(assetLogsRepository.save(any(AssetLogsModel.class))).thenReturn(null);

        String assetType = "data_asset";
        long assetId = 0;
        long eventId = 1;
        assetLogsManager.storeLogs(assetType, assetId, eventId);
        AssetLogsModel producedModel = assetLogsManager.getAssetLogsModel();
        AssetLogsModel assetLogsModel = new AssetLogsModel();
        assetLogsModel.setAssetId(assetId);
        assetLogsModel.setAssetType('d');
        assetLogsModel.setEventId(eventId);
        assertEquals("The generated asset log (asset id)", assetLogsModel.getAssetId(), producedModel.getAssetId());
        assertEquals("The generated asset log (event id)", assetLogsModel.getEventId(), producedModel.getEventId());
        assertEquals("The generated asset log (asset type)", assetLogsModel.getAssetType(), producedModel.getAssetType());

        assetType = "virtual_asset";
        assetLogsManager.storeLogs(assetType, assetId, eventId);
        producedModel = assetLogsManager.getAssetLogsModel();
        assetLogsModel = new AssetLogsModel();
        assetLogsModel.setAssetId(assetId);
        assetLogsModel.setAssetType('s');
        assetLogsModel.setEventId(eventId);
        assertEquals("The generated asset log (asset id)", assetLogsModel.getAssetId(), producedModel.getAssetId());
        assertEquals("The generated asset log (event id)", assetLogsModel.getEventId(), producedModel.getEventId());
        assertEquals("The generated asset log (asset type)", assetLogsModel.getAssetType(), producedModel.getAssetType());
    }
}
