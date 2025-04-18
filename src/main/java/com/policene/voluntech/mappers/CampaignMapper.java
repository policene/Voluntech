package com.policene.voluntech.mappers;

import com.policene.voluntech.dtos.campaigns.CampaignResponseDTO;
import com.policene.voluntech.models.entities.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    CampaignMapper INSTANCE = Mappers.getMapper(CampaignMapper.class);

    CampaignResponseDTO toCampaignResponseDTO(Campaign campaign);
    List<CampaignResponseDTO> toCampaignResponseDTOList(List<Campaign> campaigns);

}
