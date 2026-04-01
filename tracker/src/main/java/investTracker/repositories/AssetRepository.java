package investTracker.repositories;

import investTracker.models.Asset;
import investTracker.models.enums.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    Optional<Asset> findBySymbol(String symbol);

    List<Asset> findByTypeOrderByNameAsc(AssetType assetType);
    Optional<Asset> findByIsin(String isin);

}
