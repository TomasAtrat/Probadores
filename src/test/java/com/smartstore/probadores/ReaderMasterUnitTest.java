package com.smartstore.probadores;

import com.smartstore.probadores.testrfid.TagReportListenerImplementation;
import com.smartstore.probadores.ui.backend.data.entity.Branch;
import com.smartstore.probadores.ui.backend.data.entity.Reader;
import com.smartstore.probadores.ui.backend.data.entity.ReaderAntennaInBranch;
import com.smartstore.probadores.ui.backend.microservices.reader.components.ReaderMaster;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReaderMasterUnitTest {
    private ReaderMaster readerMaster;

    @Before
    public void setup() throws Exception {
        ReaderAntennaInBranch readerAntennaInBranch = setupReaderAntennaInBranch();

        readerMaster = new ReaderMaster(readerAntennaInBranch, null);
    }

    private ReaderAntennaInBranch setupReaderAntennaInBranch() {
        Reader reader = new Reader();
        reader.setId(1L);
        reader.setName("Speedwayr-10-f8-d2.local");
        reader.setAntenaPower(20.0F);
        reader.setRssi(-70f);

        Branch branch = new Branch();
        branch.setId(1L);
        branch.setDescription("Punta del este");

        ReaderAntennaInBranch readerAntennaInBranch = new ReaderAntennaInBranch();

        readerAntennaInBranch.setReader(reader);
        readerAntennaInBranch.setBranch(branch);
        readerAntennaInBranch.setFittingRoom(1);
        readerAntennaInBranch.setAntennaNumber((byte) 1);

        return readerAntennaInBranch;
    }

    @Test
    public void readerShouldSaveReaderAntennaData() {

        ReaderAntennaInBranch readerAntennaInBranch = new ReaderAntennaInBranch();

        assertNotEquals(null, readerMaster.getReaderAntennaInBranch());
    }

    @Test
    public void impinjReaderShouldBeLoadedWithReaderAntennaInBranchData() {
        String name = readerMaster.getReaderAntennaInBranch().getReader().getName();
        var impinjReader = readerMaster.getImpinjReader();

        readerMaster.configureReader();
        System.out.println("impinjReader.isConnected() = " + impinjReader.isConnected());
        assertEquals(impinjReader.getAddress(), name);
    }

    @Test
    public void ifAnyExceptionRaisedShouldDisconnectFromReader() {
        String name = readerMaster.getReaderAntennaInBranch().getReader().getName();
        var impinjReader = readerMaster.getImpinjReader();

        assertFalse(impinjReader.isConnected());
        readerMaster.configureReader();
        assertTrue(impinjReader.isConnected());
        readerMaster.configureReader();
        assertFalse(impinjReader.isConnected());
    }

    @Test
    public void readTags() {
        readerMaster.configureReader();
        readerMaster.startReader();
    }

}
