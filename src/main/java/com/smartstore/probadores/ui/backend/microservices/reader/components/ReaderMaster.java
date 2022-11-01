package com.smartstore.probadores.ui.backend.microservices.reader.components;

import com.impinj.octane.*;
import com.smartstore.probadores.testrfid.TagReportListenerImplementation;
import com.smartstore.probadores.ui.backend.data.entity.ReaderAntennaInBranch;
import com.smartstore.probadores.ui.backend.microservices.product.services.ProductService;
import com.vaadin.flow.component.UI;
import lombok.Getter;

import java.util.Scanner;

@Getter
public class ReaderMaster {
    private ReaderAntennaInBranch readerAntennaInBranch;
    private ImpinjReader impinjReader;
    private ProductService productService;
    private UI ui;

    public ReaderMaster(ReaderAntennaInBranch readerAntennaInBranch, ProductService productService) throws Exception {
        this.productService = productService;

        validateReaderAntennaInBranch(readerAntennaInBranch);

        this.readerAntennaInBranch = readerAntennaInBranch;

        impinjReader = new ImpinjReader();
    }

    private void validateReaderAntennaInBranch(ReaderAntennaInBranch readerAntennaInBranch) throws Exception {
        if (readerAntennaInBranch.getReader() == null)
            throw new Exception("El lector debe tener una dirección a la cual conectarse");
    }

    public void configureReader() {
        try {
            impinjReader.connect(readerAntennaInBranch.getReader().getName());

            Settings settings = impinjReader.queryDefaultSettings();
            ReportConfig report = settings.getReport();
            report.setIncludeAntennaPortNumber(true);
            report.setMode(ReportMode.Individual);

            settings.setReaderMode(ReaderMode.AutoSetDenseReader);
            settings.setSearchMode(SearchMode.SingleTarget);

            LowDutyCycleSettings ldc = settings.getLowDutyCycle();

            ldc.setEmptyFieldTimeoutInMs(5000);
            ldc.setFieldPingIntervalInMs(2000);
            ldc.setIsEnabled(true);

            AntennaConfigGroup antennas = settings.getAntennas();
            configureAntenna(antennas);

            impinjReader.setTagReportListener(new TagReportListenerImplementation(productService, ui));

            impinjReader.applySettings(settings);

        } catch (OctaneSdkException e) {
            e.printStackTrace();
            impinjReader.disconnect();
        }
    }

    public void startReader() {
        Runnable daemonRunner = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        impinjReader.start();
                        Scanner s = new Scanner(System.in);
                        s.nextLine();
                    } catch (OctaneSdkException e) {
                        e.printStackTrace();
                        impinjReader.disconnect();
                    }
                }
            }
        };

        Thread daemonThread = new Thread(daemonRunner);
        daemonThread.setDaemon(true);
        daemonThread.start();
    }

    public void stopReader() {
        try {
            impinjReader.stop();
            impinjReader.disconnect();
        } catch (OctaneSdkException e) {
            e.printStackTrace();
            impinjReader.disconnect();
        }
    }

    private void configureAntenna(AntennaConfigGroup antennas) throws OctaneSdkException {
        antennas.disableAll();

        var antennaNumber = readerAntennaInBranch.getAntennaNumber();

        antennas.enableById(new short[]{antennaNumber});
        antennas.getAntenna(antennaNumber).setIsMaxRxSensitivity(false);
        antennas.getAntenna(antennaNumber).setIsMaxTxPower(false);
        antennas.getAntenna(antennaNumber).setTxPowerinDbm(readerAntennaInBranch.getReader().getAntenaPower());
        antennas.getAntenna(antennaNumber).setRxSensitivityinDbm(readerAntennaInBranch.getReader().getRssi());
    }

    public void setUI(UI ui) {
        this.ui = ui;
    }
}
