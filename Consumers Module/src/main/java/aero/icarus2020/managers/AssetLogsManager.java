package aero.icarus2020.managers;

import aero.icarus2020.models.AssetLogsModel;
import aero.icarus2020.repositories.AssetLogsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssetLogsManager {

    @Autowired
    private AssetLogsRepository assetLogsRepository;

    private AssetLogsModel assetLogsModel;

    public void storeLogs(String asset_type, long asset_id, long event_id){
        // Store asset logs
        assetLogsModel = new AssetLogsModel();
        assetLogsModel.setAssetId(asset_id);
        assetLogsModel.setEventId(event_id);
        if (asset_type.equals("data_asset")) {
            assetLogsModel.setAssetType('d');
        } else{
            assetLogsModel.setAssetType('s');
        }

        this.assetLogsRepository.save(assetLogsModel);
    }

    public AssetLogsRepository getAssetLogsRepository() {
        return assetLogsRepository;
    }

    public AssetLogsModel getAssetLogsModel() {
        return assetLogsModel;
    }
}
