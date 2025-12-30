package com.techmate.techmate.hexagonal.infrastructure.service.materials.mapper;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialRequest;
import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialResponse;
import com.techmate.techmate.hexagonal.infrastructure.dto.MaterialsDTO;
import com.techmate.techmate.hexagonal.domain.entity.Materials;
import com.techmate.techmate.hexagonal.domain.entity.Role;
import com.techmate.techmate.hexagonal.domain.entity.RoleMaterials;
import com.techmate.techmate.hexagonal.domain.entity.SubCategories;
import com.techmate.techmate.hexagonal.domain.repository.RoleRepository;
import com.techmate.techmate.hexagonal.domain.repository.SubCategoriesRepository;
import com.techmate.techmate.service.RoleService;
import com.techmate.techmate.service.SubCategoriesService;

/**
 * Mapper especializado para Materials (Entity <-> DTO).
 * Extraído del servicio original para respetar SRP y facilitar pruebas.
 */
@Component
public class MaterialsMapper {

    private final SubCategoriesRepository subCategoriesRepository;
    private final RoleRepository roleRepository;
    private final SubCategoriesService subCategoriesService;
    private final RoleService roleService;

    public MaterialsMapper(SubCategoriesRepository subCategoriesRepository,
            RoleRepository roleRepository,
            SubCategoriesService subCategoriesService,
            RoleService roleService) {
        this.subCategoriesRepository = subCategoriesRepository;
        this.roleRepository = roleRepository;
        this.subCategoriesService = subCategoriesService;
        this.roleService = roleService;
    }

    // Convierte un MaterialRequest (API) a MaterialsDTO (interno)
    public MaterialsDTO fromRequest(MaterialRequest req) {
        if (req == null)
            return null;

        MaterialsDTO dto = new MaterialsDTO();
        dto.setName(req.getName());
        dto.setDescription(req.getDescription());
        if (req.getPrice() != null) {
            dto.setPrice(java.math.BigDecimal.valueOf(req.getPrice()));
        } else {
            dto.setPrice(java.math.BigDecimal.ZERO);
        }
        dto.setStock(req.getStock() != null ? req.getStock() : 0);
        dto.setSubCategoryId(req.getSubCategoryId() != null ? req.getSubCategoryId() : 0);
        dto.setRoleIds(req.getRoleIds());

        // imagePath se asigna en el controller si viene archivo multipart
        return dto;
    }

    // Convierte un MaterialsDTO interno a MaterialResponse público
    public MaterialResponse toResponse(MaterialsDTO dto, String serverUrl) {
        if (dto == null)
            return null;

        String imagePath = dto.getImagePath();
        if (imagePath != null && serverUrl != null && !serverUrl.isEmpty() && !imagePath.startsWith("http")) {
            imagePath = serverUrl + "/admin/materials/images/" + imagePath;
        }

        return new MaterialResponse(dto.getId(), imagePath, dto.getName(), dto.getDescription(),
                dto.getPrice(), dto.getStock(), dto.getBorrowableStock(), dto.getSubCategoryId(),
                dto.getSubCategoryName(), dto.getRoleNames());
    }

    public MaterialsDTO toDTO(Materials materials) {
        if (materials == null)
            return null;

        MaterialsDTO dto = new MaterialsDTO();
        dto.setId(materials.getId());
        dto.setImagePath(materials.getImagePath());
        dto.setName(materials.getName());
        dto.setDescription(materials.getDescription());
        dto.setPrice(materials.getPrice());
        dto.setStock(materials.getStock());
        dto.setBorrowableStock(materials.getBorrowableStock());

        if (materials.getSubCategory() != null) {
            dto.setSubCategoryId(materials.getSubCategory().getSubCategoryId());
            dto.setSubCategoryName(
                    subCategoriesService.getSubCategoryNameById(materials.getSubCategory().getSubCategoryId()));
        }

        List<Integer> roleIds = materials.getRoleMaterials().stream()
                .map(RoleMaterials::getRole)
                .map(Role::getRoleId)
                .collect(Collectors.toList());

        List<String> roleNames = materials.getRoleMaterials().stream()
                .map(RoleMaterials::getRole)
                .map(r -> roleService.getRoleNameById(r.getId()))
                .collect(Collectors.toList());

        dto.setRoleIds(roleIds);
        dto.setRoleNames(roleNames);

        return dto;
    }

    public Materials toEntity(MaterialsDTO materialsDTO) {
        if (materialsDTO == null)
            return null;

        Materials materials = new Materials();
        materials.setImagePath(materialsDTO.getImagePath());
        materials.setName(materialsDTO.getName());
        materials.setDescription(materialsDTO.getDescription());
        materials.setPrice(materialsDTO.getPrice());

        Integer dtoStock = materialsDTO.getStock();
        if (dtoStock == null || dtoStock.intValue() == 0) {
            materials.setStock(0);
            materials.setBorrowableStock(0);
        } else {
            materials.setStock(dtoStock);
            materials.setBorrowableStock(dtoStock);
        }

        // SubCategory
        SubCategories subCategories = subCategoriesRepository.findById(materialsDTO.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException(
                        "Subcategoría no encontrada con ID: " + materialsDTO.getSubCategoryId()));
        materials.setSubCategory(subCategories);

        // Roles
        List<RoleMaterials> roleMaterialsList = new ArrayList<>();
        if (materialsDTO.getRoleIds() != null) {
            for (Integer roleId : materialsDTO.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));

                RoleMaterials roleMaterials = new RoleMaterials();
                roleMaterials.setRole(role);
                roleMaterials.setMaterials(materials);
                roleMaterialsList.add(roleMaterials);
            }
        }
        materials.setRoleMaterials(roleMaterialsList);

        return materials;
    }
}


