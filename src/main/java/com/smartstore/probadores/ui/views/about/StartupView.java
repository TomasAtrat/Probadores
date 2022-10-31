package com.smartstore.probadores.ui.views.about;

import com.smartstore.probadores.backend.data.entity.Branch;
import com.smartstore.probadores.backend.data.entity.ReaderAntennaInBranch;
import com.smartstore.probadores.backend.microservices.FittingRoomService;
import com.smartstore.probadores.ui.views.sistemadeprobadores.SistemadeprobadoresView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "startup")
@RouteAlias(value = "")
@PageTitle("Startup | Probadores")
public class StartupView extends VerticalLayout {
    private ComboBox<Branch> branchComboBox;
    private ComboBox<Integer> fittingRoomComboBox;
    private FittingRoomService fittingRoomService;
    private Button confirmButton;

    @Autowired
    public StartupView(FittingRoomService fittingRoomService) {
        this.fittingRoomService = fittingRoomService;

        loadComboBoxes();

        setAlignItems(Alignment.CENTER);

        setUpButton();

        add(branchComboBox, fittingRoomComboBox);
    }

    private void loadComboBoxes() {
        branchComboBox = new ComboBox<>("Sucursal");
        branchComboBox.setItems(fittingRoomService.getAllBranches());
        branchComboBox.setItemLabelGenerator(branch ->
                branch.getId() + " - " + branch.getDescription());

        fittingRoomComboBox = new ComboBox<>("Probador");

        branchComboBox.addValueChangeListener(e -> {
            if (e.getValue() != null)
                fittingRoomComboBox.setItems(fittingRoomService.getAllFittingRoomsByBranch(e.getValue()));
        });
    }

    private void setUpButton() {
        confirmButton = new Button("Confirmar");
        confirmButton.addClickListener(i -> {
            ReaderAntennaInBranch readerAntennaInBranch = fittingRoomService
                    .getReaderAntennaByBranchAndFittingRoom(branchComboBox.getValue(), fittingRoomComboBox.getValue());

/*
            confirmButton.getUI().ifPresent(ui-> ui.navigate(SistemadeprobadoresView.class, readerAntennaInBranch));
*/
        });
    }

}

