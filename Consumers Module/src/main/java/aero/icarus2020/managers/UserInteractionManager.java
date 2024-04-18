package aero.icarus2020.managers;

import aero.icarus2020.models.UserInteractionModel;
import aero.icarus2020.repositories.UserInteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserInteractionManager {

    @Autowired
    private UserInteractionRepository userInteractionRepository;
    private UserInteractionModel newInteraction;

    public void storeInteractions(long org_id, long asset_id, String eventType) {

        short score = 0;
        //add a score to user interaction (used in the recommendation model)
        switch (eventType) {
            case "ASSET_STARRED":
                score = 1;
                break;
            case "ASSET_REQUESTED":
                score = 2;
                break;
            case "CONTRACT_PAID":
                score = 3;
                break;
        }

        if (score > 0) {
            newInteraction = new UserInteractionModel(org_id, asset_id, score);
            this.userInteractionRepository.save(newInteraction);
        }
    }

    public UserInteractionRepository getUserInteractionRepository() {
        return userInteractionRepository;
    }

    public UserInteractionModel getNewInteraction() {
        return newInteraction;
    }
}
