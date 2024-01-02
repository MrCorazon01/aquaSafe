package com.ept.dic1.aquasafe.views.dashboard;

import com.ept.dic1.aquasafe.data.SampleDevice;
import com.ept.dic1.aquasafe.services.SampleDeviceService;
import com.ept.dic1.aquasafe.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@PageTitle("Dispositif")
@RolesAllowed({"USER", "ADMIN"})
@Route(value = "device-dashboard/:trackingNumber", layout = MainLayout.class)
public class DeviceDashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final SampleDeviceService sampleDeviceService;
    private String trackingNumber;


    public DeviceDashboardView(SampleDeviceService sampleDeviceService) {
        this.sampleDeviceService = sampleDeviceService;


        addClassName("dashboard-view");


    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        trackingNumber = event.getRouteParameters().get("trackingNumber").orElse(null);
        if (trackingNumber != null) {
            init();
        } else {
            add(new Text("Dispositif introuvable"));
        }
    }

    private void init() {
        SampleDevice selectedDevice = sampleDeviceService.get(trackingNumber);

        // Ajoutez les composants pour afficher les détails du dispositif
        if (selectedDevice != null) {
            /*add(new Text("Tracking Number: " + selectedDevice.getTrackingNumber()));
            add(new Text("Region: " + selectedDevice.getRegion()));
            add(new Text("Latitude: " + selectedDevice.getLatitude()));
            add(new Text("Longitude: " + selectedDevice.getLongitude()));
            add(new Text("Battery Level: " + selectedDevice.getBatteryLevel()));
            add(new Text("Installation Date: " + selectedDevice.getInstallationDate()));*/
            
            Board board = new Board();
            board.addRow(createHighlight("Temperature", "27 °C", 0.0),
                    createHighlight("pH", "7 mol/L", 1.0),
                    createHighlight("Conductivity", "250 us/cm", -2.0),
                    createHighlight("Turbidity", "10 FNU" , 3.2));

            VerticalLayout layout0 = new VerticalLayout();
            layout0.add(createStatusView(19.0, trackingNumber), new Hr());
            layout0.add(createLocationView(selectedDevice.getRegion(), selectedDevice), new Hr());
            layout0.add(createBatteryView(89.0), new Hr());
            layout0.add(createContaminationView(17.0));
            layout0.setJustifyContentMode(JustifyContentMode.CENTER);
            layout0.setAlignItems(Alignment.CENTER);

            board.addRow(layout0, createRecentEvents());
            //board.addRow(createDeviceHealth(), createDeviceHealthDistribution());
            add(board);

        } else {
            add(new Text("Dispositif introuvable"));
        }
    }

    private Component createHighlight(String title, String value, Double percentage) {
        VaadinIcon icon = VaadinIcon.ARROW_UP;
        String prefix = "";
        String theme = "badge";

        if (percentage == 0) {
            prefix = "±";
        } else if (percentage > 0) {
            prefix = "+";
            theme += " success";
        } else if (percentage < 0) {
            icon = VaadinIcon.ARROW_DOWN;
            theme += " error";
        }

        H2 h2 = new H2(title);
        h2.addClassNames(LumoUtility.FontWeight.NORMAL, LumoUtility.Margin.NONE, LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);

        Span span = new Span(value);
        span.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.XXXLARGE);

        Icon i = icon.create();
        i.addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Padding.XSMALL);

        Span badge = new Span(i, new Span(prefix + percentage.toString()));
        badge.getElement().getThemeList().add(theme);

        VerticalLayout layout = new VerticalLayout(h2, span, badge);
        layout.addClassName(LumoUtility.Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);

        Span span = new Span(subtitle);
        span.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }

    private Component createRecentEvents() {
        // Header
        Select year = new Select();
        year.setItems("2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024");
        year.setValue("2023");
        year.setWidth("100px");

        HorizontalLayout header = createHeader("Paramètres de Mesure", "mois");
        header.add(year);

        // Chart
        Chart chart = new Chart(ChartType.AREASPLINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Mesures");

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);

        // Exemple : Utilisez de vraies données de mesures d'eau
        conf.addSeries(new ListSeries("Conductivité", 3.50, 3.60, 3.80, 4.00, 4.10, 3.90, 3.70, 3.60, 3.50, 3.70, 3.90, 4.10));
        conf.addSeries(new ListSeries("Température", 15, 16, 17, 18, 20, 21, 22, 23, 22, 20, 18, 16));
        conf.addSeries(new ListSeries("pH", 7.2, 7.0, 7.5, 7.8, 7.6, 7.4, 7.2, 7.1, 7.0, 7.3, 7.5, 7.7));
        conf.addSeries(new ListSeries("Turbidité", 5, 6, 8, 10, 12, 9, 7, 6, 5, 7, 9, 11));

        // Add it all together
        VerticalLayout recentEvents = new VerticalLayout(header, chart);
        recentEvents.addClassName(LumoUtility.Padding.LARGE);
        recentEvents.setPadding(false);
        recentEvents.setSpacing(false);
        recentEvents.getElement().getThemeList().add("spacing-l");
        return recentEvents;
    }

    public HorizontalLayout createStatusView(Double percentage, String trackingNumber){

        String prefix = "";
        String theme = "badge";

        if (percentage > 80) {
            prefix = "Excellent";
        } else if (percentage > 20) {
            prefix = "Ok";
            theme += " success";
        } else {
            prefix = "Failing";
            theme += " error";
        }



        Span badge = new Span(new Span(prefix));
        badge.getElement().getThemeList().add(theme);



        HorizontalLayout containerLayout = new HorizontalLayout();
        containerLayout.setWidthFull();
        setJustifyContentMode(JustifyContentMode.START);


        Span span = new Span("Etat du dispositif " + trackingNumber + " :");

        span.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD);
        containerLayout.add(span, badge);

        return containerLayout;
    }
    public HorizontalLayout createLocationView(String region, SampleDevice selectedDevice){
        HorizontalLayout containerLayout = new HorizontalLayout();
        containerLayout.setWidthFull();
        setJustifyContentMode(JustifyContentMode.START);

        Span regionName = new Span(region);
        regionName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD, LumoUtility.TextColor.PRIMARY);
        Span regionLayout = new Span(regionName);

        Button mapButton = new Button("Voir sur la carte", new Icon(VaadinIcon.MAP_MARKER));
        mapButton.addClickListener(e -> navigateToMapView(selectedDevice.getTrackingNumber()));


        Span span = new Span("Région: ");

        span.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.FontWeight.SEMIBOLD);
        containerLayout.add(span, regionLayout, mapButton);
        containerLayout.setFlexGrow(1, regionLayout);

        return containerLayout;
    }
    private void navigateToMapView(String trackingNumber) {
        Map map = new HashMap();
        map.put("trackingNumber", trackingNumber);

        UI.getCurrent().navigate("map/", QueryParameters.simple(map));
    }

    public HorizontalLayout createBatteryView(Double percentage) {


        HorizontalLayout containerLayout = new HorizontalLayout();
        containerLayout.setWidth("90%");
        containerLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout batteryLayout = new HorizontalLayout();
        batteryLayout.setWidth("auto");

        Image img = new Image("images/battery-icon.png", "placeholder battery");
        img.setWidth("70px");

        H2 h2 = new H2("Pourcentage Batterie");
        h2.addClassNames(LumoUtility.FontWeight.NORMAL, LumoUtility.Margin.NONE, LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);

        H2 batteryLevel = new H2(percentage.intValue() + " %");
        batteryLevel.getStyle().set("font-size", "70px");
        if (percentage > 80) {
            batteryLevel.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.TextColor.PRIMARY);
        } else if (percentage > 20) {
            batteryLevel.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.TextColor.SUCCESS);
        } else {
            batteryLevel.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.TextColor.ERROR);
        }



        VerticalLayout batteryLevelLayout = new VerticalLayout(h2, batteryLevel);
        batteryLevelLayout.setAlignItems(Alignment.CENTER);



        HorizontalLayout imgLayout = new HorizontalLayout(img);
        imgLayout.setWidthFull();
        imgLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        batteryLayout.setAlignItems(Alignment.CENTER);
        batteryLayout.add(imgLayout, batteryLevelLayout);
        batteryLayout.setWidthFull();

        containerLayout.add(batteryLayout);

        return containerLayout;
    }

    public HorizontalLayout createContaminationView(Double percentage) {
        HorizontalLayout containerLayout = new HorizontalLayout();
        containerLayout.setWidth("90%");
        containerLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout ContaminationLayout = new HorizontalLayout();
        ContaminationLayout.setWidth("auto");

        Image img = new Image("images/water-bottle.png", "placeholder Contamination");
        img.setWidth("200px");

        H2 h2 = new H2("Degrés de Contamination");
        h2.addClassNames(LumoUtility.FontWeight.NORMAL, LumoUtility.Margin.NONE, LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);

        H2 contaminationLevel = new H2(percentage.intValue()+ " %");
        contaminationLevel.getStyle().set("font-size", "70px");
        if (percentage > 80) {
            contaminationLevel.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.TextColor.PRIMARY);
        } else if (percentage > 20) {
            contaminationLevel.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.TextColor.SUCCESS);
        } else {
            contaminationLevel.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.TextColor.ERROR);
        }

        VerticalLayout contaminationLevelLayout = new VerticalLayout(h2, contaminationLevel);
        contaminationLevelLayout.setAlignItems(Alignment.CENTER);



        HorizontalLayout imgLayout = new HorizontalLayout(img);
        imgLayout.setWidthFull();
        imgLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        ContaminationLayout.setAlignItems(Alignment.CENTER);
        ContaminationLayout.add(contaminationLevelLayout, imgLayout);
        ContaminationLayout.setWidthFull();

        containerLayout.add(ContaminationLayout);

        return containerLayout;
    }

}


