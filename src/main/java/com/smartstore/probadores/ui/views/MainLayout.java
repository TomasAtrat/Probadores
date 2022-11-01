package com.smartstore.probadores.ui.views;


import com.smartstore.probadores.ui.views.about.AboutView;
import com.smartstore.probadores.ui.views.sistemadeprobadores.SistemadeprobadoresView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.router.RouterLink;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    public MainLayout() {
        addToNavbar(createHeaderContent());
    }

    private Component createHeaderContent() {
        Header header = new Header();
        header.addClassNames("box-border", "flex", "flex-col", "w-full");

        Div layout = new Div();
        layout.addClassNames("flex", "items-center", "px-l");

        H1 appName = new H1("Probadores");
        appName.addClassNames("my-m", "me-auto", "text-l");
        layout.add(appName);

        header.add(layout);

        return header;
    }

}
