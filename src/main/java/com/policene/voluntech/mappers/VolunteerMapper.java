package com.policene.voluntech.mappers;

import com.policene.voluntech.dtos.volunteer.ShortVolunteerResponseDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerRequestDTO;
import com.policene.voluntech.dtos.volunteer.VolunteerResponseDTO;
import com.policene.voluntech.models.entities.Volunteer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VolunteerMapper {

    VolunteerMapper INSTANCE = Mappers.getMapper(VolunteerMapper.class);

    // Como usamos lógica para transformar um Request DTO para uma entidade,
    // é melhor não usarmos o Map Struct nesse caso.
    Volunteer toVolunteer(VolunteerRequestDTO requestDTO);
    VolunteerResponseDTO toVolunteerResponseDTO(Volunteer volunteer);
    List<VolunteerResponseDTO> toVolunteerResponseDTOList(List<Volunteer> volunteers);
    ShortVolunteerResponseDTO toShortVolunteerResponseDTO(Volunteer volunteer);
    List<ShortVolunteerResponseDTO> toShortVolunteerResponseDTOList(List<Volunteer> volunteers);

}
