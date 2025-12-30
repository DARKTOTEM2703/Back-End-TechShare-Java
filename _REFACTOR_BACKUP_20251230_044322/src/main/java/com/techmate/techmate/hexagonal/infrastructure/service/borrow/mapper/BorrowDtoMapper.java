package com.techmate.techmate.hexagonal.infrastructure.service.borrow.mapper;

import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.entity.Usuario;
import com.techmate.techmate.hexagonal.domain.entity.Borrow;
import com.techmate.techmate.hexagonal.domain.entity.DetailsBorrow;
import com.techmate.techmate.hexagonal.infrastructure.dto.BorrowReadDTO;
import com.techmate.techmate.hexagonal.infrastructure.dto.DetailsBorrowDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BorrowDtoMapper {

    @Mapping(target = "id", source = "borrow.id")
    @Mapping(target = "status", source = "borrow.status")
    @Mapping(target = "date", source = "borrow.date")
    @Mapping(target = "startDate", source = "borrow.startDate")
    @Mapping(target = "endDate", source = "borrow.endDate")
    @Mapping(target = "returnDate", source = "borrow.returnDate")

    // From Usuario
    @Mapping(target = "usuarioId", source = "user.id")
    @Mapping(target = "usuarioName", source = "user.user_name")
    @Mapping(target = "usuarioEmail", source = "user.email")

    // From Materials
    @Mapping(target = "materialId", source = "material.id")
    @Mapping(target = "materialName", source = "material.name")
    BorrowReadDTO toReadDto(Borrow borrow, Usuario user, Materials material);

    // Mapea cada detalle de dominio a su DTO (MapStruct hará el mapeo de campos por
    // nombre)
    DetailsBorrowDTO detailsToDto(DetailsBorrow details);
}
