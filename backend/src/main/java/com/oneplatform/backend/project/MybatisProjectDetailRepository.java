package com.oneplatform.backend.project;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.oneplatform.backend.project.entity.OpProjectAddress;
import com.oneplatform.backend.project.entity.OpProjectInstruction;
import com.oneplatform.backend.project.mapper.ProjectAddressMapper;
import com.oneplatform.backend.project.mapper.ProjectInstructionMapper;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MybatisProjectDetailRepository implements ProjectDetailRepository {

    private final ProjectAddressMapper addressMapper;
    private final ProjectInstructionMapper instructionMapper;

    public MybatisProjectDetailRepository(ProjectAddressMapper addressMapper, ProjectInstructionMapper instructionMapper) {
        this.addressMapper = addressMapper;
        this.instructionMapper = instructionMapper;
    }

    @Override
    public List<ProjectAddressRecord> findAddresses(Long projectId) {
        return addressMapper.selectList(new LambdaQueryWrapper<OpProjectAddress>()
                        .eq(OpProjectAddress::getProjectId, projectId)
                        .eq(OpProjectAddress::getDeleted, 0)
                        .orderByAsc(OpProjectAddress::getSort)
                        .orderByAsc(OpProjectAddress::getId))
                .stream()
                .map(this::mapAddress)
                .toList();
    }

    @Override
    public List<Long> findAddressIds(Long projectId) {
        return addressMapper.selectList(new LambdaQueryWrapper<OpProjectAddress>()
                        .select(OpProjectAddress::getId)
                        .eq(OpProjectAddress::getProjectId, projectId)
                        .eq(OpProjectAddress::getDeleted, 0))
                .stream()
                .map(OpProjectAddress::getId)
                .toList();
    }

    @Override
    @Transactional
    public List<ProjectAddressRecord> replaceAddresses(Long projectId, List<ProjectAddressMutation> mutations) {
        LocalDateTime now = LocalDateTime.now();
        addressMapper.update(null, new LambdaUpdateWrapper<OpProjectAddress>()
                .eq(OpProjectAddress::getProjectId, projectId)
                .eq(OpProjectAddress::getDeleted, 0)
                .set(OpProjectAddress::getDeleted, 1)
                .set(OpProjectAddress::getUpdatedAt, now));
        for (ProjectAddressMutation mutation : mutations) {
            if (mutation.id() == null) {
                OpProjectAddress address = new OpProjectAddress();
                apply(address, projectId, mutation, now);
                address.setCreatedAt(now);
                address.setDeleted(0);
                addressMapper.insert(address);
            } else {
                addressMapper.update(null, new LambdaUpdateWrapper<OpProjectAddress>()
                        .eq(OpProjectAddress::getId, mutation.id())
                        .eq(OpProjectAddress::getProjectId, projectId)
                        .set(OpProjectAddress::getName, mutation.name())
                        .set(OpProjectAddress::getType, mutation.type())
                        .set(OpProjectAddress::getUrl, mutation.url())
                        .set(OpProjectAddress::getIsDefault, mutation.isDefault())
                        .set(OpProjectAddress::getIsDetection, mutation.isDetection())
                        .set(OpProjectAddress::getSort, mutation.sort())
                        .set(OpProjectAddress::getStatus, mutation.status())
                        .set(OpProjectAddress::getDeleted, 0)
                        .set(OpProjectAddress::getUpdatedAt, now));
            }
        }
        return findAddresses(projectId);
    }

    @Override
    public ProjectInstructionRecord findInstruction(Long projectId) {
        OpProjectInstruction instruction = instructionMapper.selectOne(new LambdaQueryWrapper<OpProjectInstruction>()
                .eq(OpProjectInstruction::getProjectId, projectId)
                .eq(OpProjectInstruction::getDeleted, 0)
                .last("LIMIT 1"));
        return instruction == null ? null : mapInstruction(instruction);
    }

    @Override
    @Transactional
    public ProjectInstructionRecord upsertInstruction(Long projectId, ProjectInstructionMutation mutation) {
        OpProjectInstruction existing = instructionMapper.selectOne(new LambdaQueryWrapper<OpProjectInstruction>()
                .eq(OpProjectInstruction::getProjectId, projectId)
                .last("LIMIT 1"));
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            OpProjectInstruction instruction = new OpProjectInstruction();
            apply(instruction, projectId, mutation, now);
            instruction.setCreatedAt(now);
            instruction.setDeleted(0);
            instructionMapper.insert(instruction);
        } else {
            instructionMapper.update(null, new LambdaUpdateWrapper<OpProjectInstruction>()
                    .eq(OpProjectInstruction::getId, existing.getId())
                    .set(OpProjectInstruction::getBrowserRequirement, mutation.browserRequirement())
                    .set(OpProjectInstruction::getVpnRequirement, mutation.vpnRequirement())
                    .set(OpProjectInstruction::getNotes, mutation.notes())
                    .set(OpProjectInstruction::getMaintainer, mutation.maintainer())
                    .set(OpProjectInstruction::getContactPhone, mutation.contactPhone())
                    .set(OpProjectInstruction::getDeleted, 0)
                    .set(OpProjectInstruction::getUpdatedAt, now));
        }
        return findInstruction(projectId);
    }

    private ProjectAddressRecord mapAddress(OpProjectAddress address) {
        return new ProjectAddressRecord(
                address.getId(),
                address.getProjectId(),
                address.getName(),
                address.getType(),
                address.getUrl(),
                address.getIsDefault(),
                address.getIsDetection(),
                address.getSort(),
                address.getStatus(),
                address.getUpdatedAt()
        );
    }

    private ProjectInstructionRecord mapInstruction(OpProjectInstruction instruction) {
        return new ProjectInstructionRecord(
                instruction.getId(),
                instruction.getProjectId(),
                instruction.getBrowserRequirement(),
                instruction.getVpnRequirement(),
                instruction.getNotes(),
                instruction.getMaintainer(),
                instruction.getContactPhone(),
                instruction.getUpdatedAt()
        );
    }

    private void apply(OpProjectAddress address, Long projectId, ProjectAddressMutation mutation, LocalDateTime now) {
        address.setProjectId(projectId);
        address.setName(mutation.name());
        address.setType(mutation.type());
        address.setUrl(mutation.url());
        address.setIsDefault(mutation.isDefault());
        address.setIsDetection(mutation.isDetection());
        address.setSort(mutation.sort());
        address.setStatus(mutation.status());
        address.setUpdatedAt(now);
    }

    private void apply(OpProjectInstruction instruction, Long projectId, ProjectInstructionMutation mutation, LocalDateTime now) {
        instruction.setProjectId(projectId);
        instruction.setBrowserRequirement(mutation.browserRequirement());
        instruction.setVpnRequirement(mutation.vpnRequirement());
        instruction.setNotes(mutation.notes());
        instruction.setMaintainer(mutation.maintainer());
        instruction.setContactPhone(mutation.contactPhone());
        instruction.setUpdatedAt(now);
    }
}
