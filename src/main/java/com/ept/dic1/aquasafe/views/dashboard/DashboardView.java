package com.ept.dic1.aquasafe.views.dashboard;

import com.ept.dic1.aquasafe.data.SampleDevice;
import com.ept.dic1.aquasafe.views.MainLayout;
import com.ept.dic1.aquasafe.views.dispositifs.DeviceHealth;
import com.ept.dic1.aquasafe.views.dispositifs.DeviceHealth.Status;
import com.ept.dic1.aquasafe.views.dispositifs.DeviceService;
import com.ept.dic1.aquasafe.views.dispositifs.DevicesView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;

import java.util.List;

@PageTitle("Dashboard")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "dashboard", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})
public class DashboardView extends Main {
    private final DeviceService deviceService;
    public DashboardView(DeviceService deviceService) {
        this.deviceService = deviceService;
        addClassName("dashboard-view");

        Board board = new Board();
        board.addRow(createHighlight("Nombre total de dispositifs installés", "150", 0.0),
                createHighlight("Taux de contamination global", "12%", 5.0),
                createHighlight("Niveau moyen de batterie des dispositifs", "75%", -2.0),
                createHighlight("Dispositifs signalant une contamination", "10", 3.2));
        board.addRow(createRecentEvents());
        board.addRow(createDeviceHealth(), createDeviceHealthDistribution());
        add(board);
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
        h2.addClassNames(FontWeight.NORMAL, Margin.NONE, TextColor.SECONDARY, FontSize.XSMALL);

        Span span = new Span(value);
        span.addClassNames(FontWeight.SEMIBOLD, FontSize.XXXLARGE);

        Icon i = icon.create();
        i.addClassNames(BoxSizing.BORDER, Padding.XSMALL);

        Span badge = new Span(i, new Span(prefix + percentage.toString()));
        badge.getElement().getThemeList().add(theme);

        VerticalLayout layout = new VerticalLayout(h2, span, badge);
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
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
        recentEvents.addClassName(Padding.LARGE);
        recentEvents.setPadding(false);
        recentEvents.setSpacing(false);
        recentEvents.getElement().getThemeList().add("spacing-l");
        return recentEvents;
    }



    private Component createDeviceHealth() {
        // Header
        HorizontalLayout header = createHeader("État de santé des Dispositifs", "Métriques des dispositifs");
        Button button = new Button(new Icon(VaadinIcon.OPTION_A));
        button.setText("Dispositifs");
        button.addClickListener(e -> UI.getCurrent().navigate(DevicesView.class));
        header.add(button);

        // Grid
        Grid<SampleDevice> grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);

        int page = 0;
        int pageSize = 10;

        Page<SampleDevice> devicesPage = deviceService.getAllDevices(page, pageSize);
        List<SampleDevice> devices = devicesPage.getContent();

        grid.addColumn(new ComponentRenderer<>(device -> {
            Span status = new Span();
            String statusText = getStatusDisplayName(device);
            status.getElement().setAttribute("aria-label", "Status: " + statusText);
            status.getElement().setAttribute("title", "Status: " + statusText);
            status.getElement().getThemeList().add(getStatusTheme(device));
            return status;
        })).setHeader("").setFlexGrow(0).setAutoWidth(true);
        grid.addColumn(SampleDevice::getTrackingNumber).setHeader("Dispositif ID").setFlexGrow(1);
        grid.addColumn(device -> {
                    DeviceHealth deviceHealth = new DeviceHealth(device);
                    return deviceHealth.getContaminantLevel();
                }).setHeader("Niveau de Contamination").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);
        grid.addColumn(SampleDevice::getBatteryLevel).setHeader("Niveau de Batterie").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.setItems(devices);

        // Add it all together
        VerticalLayout deviceHealth = new VerticalLayout(header, grid);
        deviceHealth.addClassName(Padding.LARGE);
        deviceHealth.setPadding(false);
        deviceHealth.setSpacing(false);
        deviceHealth.getElement().getThemeList().add("spacing-l");
        return deviceHealth;
    }

    private Component createDeviceHealthDistribution() {
        HorizontalLayout header = createHeader("Répartition des Dispositifs par État de Santé", "Vue d'ensemble de la santé des dispositifs");

        // Chart
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);


        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("Excellent", 30));
        series.add(new DataSeriesItem("OK", 60));
        series.add(new DataSeriesItem("En échec", 10));
        conf.addSeries(series);

        // Add it all together
        VerticalLayout deviceHealthDistribution = new VerticalLayout(header, chart);
        deviceHealthDistribution.addClassName(Padding.LARGE);
        deviceHealthDistribution.setPadding(false);
        deviceHealthDistribution.setSpacing(false);
        deviceHealthDistribution.getElement().getThemeList().add("spacing-l");
        return deviceHealthDistribution;
    }


    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassNames(FontSize.XLARGE, Margin.NONE);

        Span span = new Span(subtitle);
        span.addClassNames(TextColor.SECONDARY, FontSize.XSMALL);

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }

    private String getStatusDisplayName(SampleDevice device) {
        DeviceHealth deviceHealth = new DeviceHealth(device);
        Status status = deviceHealth.getStatus();
        if (status == Status.OK) {
            return "Ok";
        } else if (status == Status.FAILING) {
            return "Échec";
        } else if (status == Status.EXCELLENT) {
            return "Excellent";
        } else {
            return status.toString();
        }
    }

    private String getStatusTheme(SampleDevice device) {
        DeviceHealth deviceHealth = new DeviceHealth(device);
        Status status = deviceHealth.getStatus();
        String theme = "badge primary small";
        if (status == Status.EXCELLENT) {
            theme += " success";
        } else if (status == Status.FAILING) {
            theme += " error";
        }
        return theme;
    }

}
