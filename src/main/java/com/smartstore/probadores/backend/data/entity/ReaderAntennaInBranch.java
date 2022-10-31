package com.smartstore.probadores.backend.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "reader_antenna_in_branch")
public class ReaderAntennaInBranch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;

    @Column(name = "antenna_number", nullable = false)
    private Byte antennaNumber;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "fitting_room", nullable = false)
    private Integer fittingRoom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Byte getAntennaNumber() {
        return antennaNumber;
    }

    public void setAntennaNumber(Byte antennaNumber) {
        this.antennaNumber = antennaNumber;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Integer getFittingRoom() {
        return fittingRoom;
    }

    public void setFittingRoom(Integer fittingRoom) {
        this.fittingRoom = fittingRoom;
    }

}