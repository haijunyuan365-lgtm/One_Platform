package com.oneplatform.backend.project;

import java.util.List;

public interface ProjectDetailRepository {

    List<ProjectAddressRecord> findAddresses(Long projectId);

    List<Long> findAddressIds(Long projectId);

    List<ProjectAddressRecord> replaceAddresses(Long projectId, List<ProjectAddressMutation> mutations);

    ProjectInstructionRecord findInstruction(Long projectId);

    ProjectInstructionRecord upsertInstruction(Long projectId, ProjectInstructionMutation mutation);
}
