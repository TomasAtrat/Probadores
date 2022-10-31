package com.smartstore.probadores.backend.repositories;

import com.smartstore.probadores.backend.data.entity.Branch;
import com.smartstore.probadores.backend.data.entity.ReaderAntennaInBranch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReaderAntennaInBranchRepository extends JpaRepository<ReaderAntennaInBranch, Long> {
    List<ReaderAntennaInBranch> findAllByBranch(Branch branchId);

    ReaderAntennaInBranch findByBranchAndFittingRoom(Branch branch, Integer fittingRoom);
}