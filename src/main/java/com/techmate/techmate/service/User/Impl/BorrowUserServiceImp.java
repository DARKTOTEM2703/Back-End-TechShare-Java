// BorrowUserServiceImp.java
package com.techmate.techmate.service.User.Impl;

import java.util.stream.Collectors;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.dto.DetailsBorrowDTO;
import com.techmate.techmate.entity.Borrow;
import com.techmate.techmate.entity.DetailsBorrow;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.RoleMaterials;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.repository.*;
import com.techmate.techmate.security.TokenUtils;
import com.techmate.techmate.service.User.BorrowUserService;
import com.techmate.techmate.service.borrow.mapper.BorrowMapper;

@Service
public class BorrowUserServiceImp implements BorrowUserService {

    private final BorrowRepository borrowRepository;
    private final MaterialsRepository materialsRepository;
    private final DetailsBorrowRepository detailsBorrowRepository;
    private final UsuarioRepository usuarioRepository;
    private final RoleMaterialsRepository roleMaterialsRepository;
    private final BorrowMapper borrowMapper;

    public BorrowUserServiceImp(BorrowRepository borrowRepository, MaterialsRepository materialsRepository,
            DetailsBorrowRepository detailsBorrowRepository, UsuarioRepository usuarioRepository,
            RoleMaterialsRepository roleMaterialsRepository, BorrowMapper borrowMapper) {
        this.borrowRepository = borrowRepository;
        this.materialsRepository = materialsRepository;
        this.detailsBorrowRepository = detailsBorrowRepository;
        this.usuarioRepository = usuarioRepository;
        this.roleMaterialsRepository = roleMaterialsRepository;
        this.borrowMapper = borrowMapper;
    }

    private Borrow convertToEntity(BorrowDTO borrowDTO) {
        Borrow borrow = new Borrow();
        borrow.setId(borrowDTO.getId());
        borrow.setDate(borrowDTO.getDate());
        borrow.setStatus(Status.PENDING);
        borrow.setAmount(borrowDTO.getAmount());
        borrow.setDetails(borrowDTO.getDetails().stream()
                .map(detailDTO -> convertDetailsBorrowToEntity(detailDTO, borrow))
                .collect(Collectors.toList()));

        Usuario user = usuarioRepository.findById(borrowDTO.getUsuarioId())
                .orElseThrow(() -> new com.techmate.techmate.exception.BusinessException("USER_NOT_FOUND",
                        "Usuario no encontrado con ID: " + borrowDTO.getUsuarioId()));
        borrow.setUsuario(user);

        return borrow;
    }

    private DetailsBorrow convertDetailsBorrowToEntity(DetailsBorrowDTO detailDTO, Borrow borrow) {
        DetailsBorrow detailsBorrow = new DetailsBorrow();
        detailsBorrow.setBorrow(borrow);
        detailsBorrow.setQuantity(detailDTO.getQuantity());

        Materials material = materialsRepository.findById(detailDTO.getId())
                .orElseThrow(
                        () -> new com.techmate.techmate.exception.BusinessException("MATERIAL_NOT_FOUND",
                                "Material no encontrado con ID: " + detailDTO.getId()));
        detailsBorrow.setMaterials(material);
        detailsBorrow.setUnitPrice(material.getPrice());
        detailsBorrow.setTotalPrice(material.getPrice() * detailDTO.getQuantity());

        return detailsBorrow;
    }

    @Override
    @Transactional
    public BorrowDTO createBorrowDTO(BorrowDTO borrowDTO, List<Integer> roles) throws Exception {
        if (roles == null || roles.isEmpty()) {
            throw new com.techmate.techmate.exception.BusinessException("USER_NO_ROLES",
                    "El usuario no tiene roles asignados. No se puede crear el préstamo.");
        }

        // Verificar primero si todos los materiales en los detalles tienen roles
        // asignados
        for (DetailsBorrowDTO detailDTO : borrowDTO.getDetails()) {
            // Buscar el material por su ID
            Materials material = materialsRepository.findById(detailDTO.getId())
                    .orElseThrow(() -> new com.techmate.techmate.exception.BusinessException("MATERIAL_NOT_FOUND",
                            "Material no encontrado con ID: " + detailDTO.getId()));

            // Obtener la lista de roles permitidos para ese material
            List<RoleMaterials> roleMaterialsList = roleMaterialsRepository.findByMaterials(material);

            // Verificar si el material tiene roles asignados
            if (roleMaterialsList.isEmpty()) {
                throw new Exception("El material " + material.getName() + " (ID: " + material.getId()
                        + ") no tiene roles permitidos asignados.");
            }
        }

        // Convertir el DTO a entidad Borrow
        Borrow borrow = convertToEntity(borrowDTO);
        borrow.setAmount(0); // Inicializar el monto total en 0

        // Guardar el préstamo en la base de datos
        borrow = borrowRepository.save(borrow);
        double totalAmount = 0; // Inicializar el monto total

        // Iterar sobre los detalles del préstamo
        for (DetailsBorrowDTO detailDTO : borrowDTO.getDetails()) {
            // Buscar el material por su ID
            Materials material = materialsRepository.findById(detailDTO.getId())
                    .orElseThrow(() -> new Exception("Material no encontrado con ID: " + detailDTO.getId()));

            // Verificar si hay suficiente stock del material
            if (material.getBorrowableStock() < detailDTO.getQuantity()) {
                throw new com.techmate.techmate.exception.BorrowBusinessException("BORROW_INSUFFICIENT_STOCK",
                        "Stock insuficiente para el material con ID: " + material.getId());
            }

            // Obtener la lista de roles permitidos para ese material
            List<RoleMaterials> roleMaterialsList = roleMaterialsRepository.findByMaterials(material);

            // Verificar si el material tiene roles asignados
            if (roleMaterialsList.isEmpty()) {
                throw new com.techmate.techmate.exception.BusinessException("MATERIAL_NO_ROLES",
                        "El material " + material.getName() + " (ID: " + material.getId()
                                + ") no tiene roles permitidos asignados.");
            }

            // Obtener los IDs de los roles permitidos
            List<Integer> rolesPermitidos = roleMaterialsList.stream()
                    .map(roleMaterials -> roleMaterials.getRole().getId())
                    .collect(Collectors.toList());

            // Verificar si el usuario tiene al menos un rol permitido para este material
            boolean tieneRolPermitido = roles.stream().anyMatch(rolesPermitidos::contains);
            if (!tieneRolPermitido) {
                throw new com.techmate.techmate.exception.BusinessException("USER_NO_PERMISSION",
                        "El usuario no tiene permisos para acceder al material: " + material.getName() +
                                " (ID: " + material.getId() + ")");
            }

            // Crear la entidad DetailsBorrow para el detalle del préstamo
            DetailsBorrow detailsBorrow = convertDetailsBorrowToEntity(detailDTO, borrow);

            // Calcular el monto total para este material (precio unitario * cantidad)
            double detalleTotalPrice = material.getPrice() * detailDTO.getQuantity();
            detailsBorrow.setTotalPrice(detalleTotalPrice); // Asegúrate de que este campo exista en la entidad

            // Acumular el monto total en la variable totalAmount
            totalAmount += detalleTotalPrice;

            // Guardar el detalle del préstamo en la base de datos
            detailsBorrowRepository.save(detailsBorrow);

            // Actualizar el stock del material
            materialsRepository.save(material);
        }

        // Actualizar el monto total del préstamo
        borrow.setAmount(totalAmount);

        // Guardar nuevamente el préstamo actualizado con el monto total
        borrow = borrowRepository.save(borrow);

        // Convertir la entidad Borrow a DTO y devolverla
        return borrowMapper.toDTO(borrow);
    }

    @Override
    public Integer getUserIdFromToken(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }

    @Override
    public List<BorrowDTO> getAllBorrowsByUserId(Integer userId) {
        List<Borrow> borrows = borrowRepository.findByUsuarioId(userId);
        return borrows.stream()
                .map(borrowMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<Integer>> getRolesFromToken(String token) {
        return TokenUtils.getRolesFromToken(token);
    }
}
