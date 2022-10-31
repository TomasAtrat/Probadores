package com.smartstore.probadores.ui.views.startup;

import com.smartstore.probadores.ui.backend.data.entity.Branch;
import com.smartstore.probadores.ui.backend.data.entity.ReaderAntennaInBranch;
import com.smartstore.probadores.ui.backend.microservices.reader.services.ReaderService;
import com.smartstore.probadores.ui.views.MainLayout;
import com.smartstore.probadores.ui.views.sistemadeprobadores.SistemadeprobadoresView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Startup")
@Route(value = "", layout = MainLayout.class)
public class StartupView extends VerticalLayout {

    private ComboBox<Branch> branchComboBox;
    private ComboBox<Integer> fittingRoomComboBox;
    private ReaderService readerService;
    private Button confirmButton;

    public StartupView(ReaderService readerService) {
        this.readerService = readerService;

        setAlignItems(Alignment.CENTER);

        loadComboBoxes();

        setupButton();

        add(new H2("Configuración"), branchComboBox, fittingRoomComboBox, confirmButton);
    }

    private void setupButton() {
        confirmButton = new Button("Confirmar");
        confirmButton.addClickListener(e-> {
            ReaderAntennaInBranch readerAntennaInBranch =
                    readerService.findByBranchAndFittingRoom(branchComboBox.getValue(),
                            fittingRoomComboBox.getValue());

            confirmButton.getUI().ifPresent(ui-> ui.navigate(SistemadeprobadoresView.class));
        });
    }

    private void loadComboBoxes() {
        branchComboBox = new ComboBox<>("Sucursal");
        branchComboBox.setItems(readerService.findAllBranches());
        branchComboBox.setItemLabelGenerator(i -> i.getId() + " - " + i.getDescription());

        fittingRoomComboBox = new ComboBox<>("Probadores");

        branchComboBox.addValueChangeListener(e -> {
            if (e.getValue() != null)
                fittingRoomComboBox.setItems(readerService.findAllFittingRoomsByBranch(e.getValue()));
        });
    }
}
