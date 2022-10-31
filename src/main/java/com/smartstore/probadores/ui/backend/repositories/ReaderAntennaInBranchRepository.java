package com.smartstore.probadores.ui.backend.repositories;

import com.smartstore.probadores.ui.backend.data.entity.Branch;
import com.smartstore.probadores.ui.backend.data.entity.ReaderAntennaInBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReaderAntennaInBranchRepository extends JpaRepository<ReaderAntennaInBranch, Long> {
    List<ReaderAntennaInBranch> findAllByBranch(Branch branch);

    ReaderAntennaInBranch findAllByBranchAndFittingRoom(Branch branch, Integer fittingRoom);
}