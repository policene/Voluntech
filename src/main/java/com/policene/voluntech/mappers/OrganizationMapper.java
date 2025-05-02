package com.policene.voluntech.mappers;

import com.policene.voluntech.dtos.organization.OrganizationResponseDTO;
import com.policene.voluntech.models.entities.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

    OrganizationResponseDTO toOrganizationResponseDTO(Organization organization);
    List<OrganizationResponseDTO> toOrganizationResponseDTOList(List<Organization> organizations);
    Organization toOrganization(OrganizationResponseDTO organizationResponseDTO);

}
