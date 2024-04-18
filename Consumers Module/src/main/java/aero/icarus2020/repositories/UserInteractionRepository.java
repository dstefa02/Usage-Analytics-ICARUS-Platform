package aero.icarus2020.repositories;

import aero.icarus2020.models.UserInteractionModel;
import org.springframework.data.repository.CrudRepository;

public interface UserInteractionRepository extends CrudRepository<UserInteractionModel, Long> {
}
