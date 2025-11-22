package com.techmate.techmate.service.materials.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

import com.techmate.techmate.dto.MaterialsDTO;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.entity.RoleMaterials;
import com.techmate.techmate.entity.SubCategories;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.SubCategoriesRepository;
import com.techmate.techmate.service.RoleService;
import com.techmate.techmate.service.SubCategoriesService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MaterialsMapperTest {

    @Mock
    SubCategoriesRepository subCategoriesRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    SubCategoriesService subCategoriesService;

    @Mock
    RoleService roleService;

    @InjectMocks
    MaterialsMapper materialsMapper;

    @Test
    void toDTO_null_returnsNull() {
        assertThat(materialsMapper.toDTO(null)).isNull();
    }

    @Test
    void toDTO_fullMapping() {
        // construir entidad
        Materials m = new Materials();
        m.setId(1);
        m.setName("Resistor");
        m.setImagePath("img.jpg");
        SubCategories sc = new SubCategories();
        sc.setSubCategoryId(2);
        m.setSubCategory(sc);

        Role r = new Role();
        r.setId(3);
        r.setNombre("student");
        RoleMaterials rm = new RoleMaterials();
        rm.setRole(r);
        rm.setMaterials(m);
        m.setRoleMaterials(Arrays.asList(rm));

        when(subCategoriesService.getSubCategoryNameById(2)).thenReturn("Electronics");
        when(roleService.getRoleNameById(3)).thenReturn("student");

        MaterialsDTO dto = materialsMapper.toDTO(m);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Resistor");
        assertThat(dto.getSubCategoryId()).isEqualTo(2);
        assertThat(dto.getSubCategoryName()).isEqualTo("Electronics");
        assertThat(dto.getRoleIds()).containsExactly(3);
        assertThat(dto.getRoleNames()).containsExactly("student");
    }

    @Test
    void toEntity_null_returnsNull() {
        assertThat(materialsMapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_fullMapping_resolvesRelations() {
        MaterialsDTO dto = new MaterialsDTO();
        dto.setImagePath("img.jpg");
        dto.setName("Capacitor");
        dto.setPrice(1.5);
        dto.setStock(10);
        dto.setSubCategoryId(5);
        dto.setRoleIds(Arrays.asList(7));

        SubCategories sc = new SubCategories();
        sc.setSubCategoryId(5);
        when(subCategoriesRepository.findById(5)).thenReturn(Optional.of(sc));

        Role role = new Role();
        role.setId(7);
        when(roleRepository.findById(7)).thenReturn(Optional.of(role));

        Materials m = materialsMapper.toEntity(dto);

        assertThat(m).isNotNull();
        assertThat(m.getName()).isEqualTo("Capacitor");
        assertThat(m.getStock()).isEqualTo(10);
        assertThat(m.getBorrowable_stock()).isEqualTo(10);
        assertThat(m.getSubCategory()).isNotNull();
        assertThat(m.getRoleMaterials()).hasSize(1);
        assertThat(m.getRoleMaterials().get(0).getRole().getId()).isEqualTo(7);
    }
}
