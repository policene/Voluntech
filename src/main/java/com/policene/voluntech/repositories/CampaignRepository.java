package com.policene.voluntech.repositories;

import com.policene.voluntech.models.entities.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
}
