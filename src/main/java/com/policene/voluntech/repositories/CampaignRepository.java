package com.policene.voluntech.repositories;

import com.policene.voluntech.models.entities.Campaign;
import com.policene.voluntech.models.entities.Organization;
import com.policene.voluntech.models.enums.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByStatus (CampaignStatus status);

    @Query("SELECT c FROM Campaign c WHERE c.organization.id = :organizationId and c.status = 'ACCEPTED'")
    List<Campaign> findByOrganization (@Param("organizationId") Long id);

    @Query("SELECT c from Campaign c JOIN c.volunteers v WHERE v.id = :volunteerId")
    List<Campaign> findVolunteerSubscribedCampaigns(@Param("volunteerId") Long id);


}
