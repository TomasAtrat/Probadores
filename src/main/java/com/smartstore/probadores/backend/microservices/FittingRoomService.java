package com.smartstore.probadores.backend.microservices;

import com.smartstore.probadores.backend.data.entity.Branch;
import com.smartstore.probadores.backend.data.entity.ReaderAntennaInBranch;
import com.smartstore.probadores.backend.repositories.ReaderAntennaInBranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FittingRoomService {
    private ReaderAntennaInBranchRepository readerAntennaInBranchRepository;

    @Autowired
    public FittingRoomService(ReaderAntennaInBranchRepository readerAntennaInBranchRepository) {
        this.readerAntennaInBranchRepository = readerAntennaInBranchRepository;
    }

    public List<Branch> getAllBranches(){
        return this.readerAntennaInBranchRepository.findAll()
                .stream().map(ReaderAntennaInBranch::getBranch).toList();
    }

    public List<Integer> getAllFittingRoomsByBranch(Branch branch) {
        return this.readerAntennaInBranchRepository.findAllByBranch(branch)
                .stream().map(ReaderAntennaInBranch::getFittingRoom).toList();
    }

    public ReaderAntennaInBranch getReaderAntennaByBranchAndFittingRoom(Branch branch, Integer fittingRoom) {
        return this.readerAntennaInBranchRepository.findByBranchAndFittingRoom(branch, fittingRoom);
    }
}
