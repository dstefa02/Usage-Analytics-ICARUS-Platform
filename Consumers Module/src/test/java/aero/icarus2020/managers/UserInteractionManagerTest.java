package aero.icarus2020.managers;

import aero.icarus2020.models.OrganizationLogsModel;
import aero.icarus2020.models.UserInteractionModel;
import aero.icarus2020.repositories.UserInteractionRepository;
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
public class UserInteractionManagerTest {

    @MockBean
    private UserInteractionRepository userInteractionRepository;

    @Autowired
    private UserInteractionManager userInteractionManager;

    /**
     * This test method checks that the storeInteractions method will create and store correctly the interaction.
     */
    @Test
    public void storeInteractionsTest() {
        // mock userInteractionRepository
        when(userInteractionRepository.save(any(UserInteractionModel.class))).thenReturn(null);

        long orgId = 0;
        long assetId = 1;
        String eventType = "ASSET_STARRED";
        userInteractionManager.storeInteractions(orgId, assetId, eventType);
        UserInteractionModel producedModel = userInteractionManager.getNewInteraction();
        UserInteractionModel expectedModel = new UserInteractionModel();
        expectedModel.setOrganization_id(orgId);
        expectedModel.setScore((short) 1);
        expectedModel.setAsset_id(assetId);
        assertEquals("The generated interaction log (asset id)", expectedModel.getAsset_id(), producedModel.getAsset_id());
        assertEquals("The generated interaction log (score)", expectedModel.getScore(), producedModel.getScore());
        assertEquals("The generated interaction log (organization id)", expectedModel.getOrganization_id(), producedModel.getOrganization_id());

        eventType = "ASSET_REQUESTED";
        userInteractionManager.storeInteractions(orgId, assetId, eventType);
        producedModel = userInteractionManager.getNewInteraction();
        expectedModel = new UserInteractionModel();
        expectedModel.setOrganization_id(orgId);
        expectedModel.setScore((short) 2);
        expectedModel.setAsset_id(assetId);
        assertEquals("The generated interaction log (asset id)", expectedModel.getAsset_id(), producedModel.getAsset_id());
        assertEquals("The generated interaction log (score)", expectedModel.getScore(), producedModel.getScore());
        assertEquals("The generated interaction log (organization id)", expectedModel.getOrganization_id(), producedModel.getOrganization_id());

        eventType = "CONTRACT_PAID";
        userInteractionManager.storeInteractions(orgId, assetId, eventType);
        producedModel = userInteractionManager.getNewInteraction();
        expectedModel = new UserInteractionModel();
        expectedModel.setOrganization_id(orgId);
        expectedModel.setScore((short) 3);
        expectedModel.setAsset_id(assetId);
        assertEquals("The generated interaction log (asset id)", expectedModel.getAsset_id(), producedModel.getAsset_id());
        assertEquals("The generated interaction log (score)", expectedModel.getScore(), producedModel.getScore());
        assertEquals("The generated interaction log (organization id)", expectedModel.getOrganization_id(), producedModel.getOrganization_id());
    }
}
