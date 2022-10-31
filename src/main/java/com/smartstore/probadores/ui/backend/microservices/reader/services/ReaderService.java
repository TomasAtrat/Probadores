package com.smartstore.probadores.ui.backend.microservices.reader.services;

import com.smartstore.probadores.ui.backend.data.entity.Branch;
import com.smartstore.probadores.ui.backend.data.entity.ReaderAntennaInBranch;
import com.smartstore.probadores.ui.backend.repositories.ReaderAntennaInBranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReaderService {
    private ReaderAntennaInBranchRepository readerAntennaInBranchRepository;

    @Autowired
    public ReaderService(ReaderAntennaInBranchRepository readerAntennaInBranchRepository){
        this.readerAntennaInBranchRepository = readerAntennaInBranchRepository;
    }

    public List<Branch> findAllBranches(){
        return this.readerAntennaInBranchRepository.findAll()
                .stream().map(ReaderAntennaInBranch::getBranch).distinct().toList();
    }

    public List<Integer> findAllFittingRoomsByBranch(Branch branch){
        return this.readerAntennaInBranchRepository.findAllByBranch(branch)
                .stream().map(ReaderAntennaInBranch::getFittingRoom).toList();
    }

    public ReaderAntennaInBranch findByBranchAndFittingRoom(Branch branch, Integer fittingRoom){
        return this.readerAntennaInBranchRepository.findAllByBranchAndFittingRoom(branch, fittingRoom);
    }

}
