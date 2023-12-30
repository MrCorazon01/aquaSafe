package com.ept.dic1.aquasafe.views.dispositifs;

import com.ept.dic1.aquasafe.data.SampleDevice;
import com.ept.dic1.aquasafe.services.SampleDeviceService;
import com.ept.dic1.aquasafe.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Dispositifs")
@Route(value = "dispositifs", layout = MainLayout.class)
@Uses(Icon.class)
@RolesAllowed({"ADMIN", "USER"})
public class DevicesView extends Div {

    private Grid<SampleDevice> grid;

    private Filters filters;
    private final SampleDeviceService sampleDeviceService;


    public DevicesView(SampleDeviceService sampleDeviceService) {
        this.sampleDeviceService = sampleDeviceService;
        setSizeFull();
        addClassNames("dispositifs-view");



        filters = new Filters(() -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    public static class Filters extends Div implements Specification<SampleDevice> {

        private final TextField trackingNumber = new TextField("Tracking Number");
        private final Select<String> region = new Select<>();


        private final DatePicker startDate = new DatePicker("Installation Date");
        private final DatePicker endDate = new DatePicker();
        Button addDeviceButton;
        private final MultiSelectComboBox<String> parameters = new MultiSelectComboBox<>("Parameters");


        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                    LumoUtility.BoxSizing.BORDER);

            // Set placeholder and options for parameters
            parameters.setPlaceholder("Select Parameters");
            parameters.setItems("Temperature", "Conductivity", "Turbidity", "pH");
            parameters.addClassName("double-width");



            addDeviceButton = new Button("Ajouter un dispositif");
            addDeviceButton.setIcon(new Icon("lumo", "plus"));
            addDeviceButton.addClickListener(e -> {
                UI.getCurrent().navigate("ajouter-dispositif");
            });

            // Action buttons
            Button resetBtn = new Button("Réinitialiser");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                trackingNumber.clear();
                region.clear();
                startDate.clear();
                endDate.clear();
                parameters.clear();
                onSearch.run();
            });

            Button searchBtn = new Button("Rechercher");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            region.setLabel("Region");
            setRegionData(region);

            add(trackingNumber, region, createDateRangeFilter(), parameters, addDeviceButton, actions);
        }

        private void setRegionData(Select<String> select) {
            List<String> senegalRegions = List.of(
                    "Dakar", "Thiès", "Diourbel", "Louga", "Fatick", "Saint-Louis", "Kaolack", "Kaffrine",
                    "Tambacounda", "Kolda", "Ziguinchor", "Sédhiou", "Matam"
            );
            select.setItems(senegalRegions);
        }

        private Component createDateRangeFilter() {
            startDate.setPlaceholder("From");
            endDate.setPlaceholder("To");

            // For screen readers
            startDate.setAriaLabel("From date");
            endDate.setAriaLabel("To date");

            FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }

        @Override
        public Predicate toPredicate(Root<SampleDevice> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            if (!trackingNumber.isEmpty()) {
                String lowerCaseFilter = trackingNumber.getValue().toLowerCase();
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("trackingNumber")),
                        "%" + lowerCaseFilter + "%"));
            }
            if (!region.isEmpty()) {
                String lowerCaseFilter = region.getValue().toLowerCase();
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("region")),
                        "%" + lowerCaseFilter + "%"));
            }
            if (startDate.getValue() != null) {
                String databaseColumn = "installationDate";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(databaseColumn),
                        criteriaBuilder.literal(startDate.getValue())));
            }
            if (endDate.getValue() != null) {
                String databaseColumn = "installationDate";
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.literal(endDate.getValue()),
                        root.get(databaseColumn)));
            }
            if (!parameters.isEmpty()) {
                String databaseColumn = "parameters"; // Update with the correct field name for parameters
                List<Predicate> parameterPredicates = new ArrayList<>();
                for (String parameter : parameters.getValue()) {
                    parameterPredicates
                            .add(criteriaBuilder.equal(criteriaBuilder.literal(parameter), root.get(databaseColumn)));
                }
                predicates.add(criteriaBuilder.or(parameterPredicates.toArray(Predicate[]::new)));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private Component createGrid() {
        grid = new Grid<>(SampleDevice.class, false);

        grid.addColumn(new ComponentRenderer<>(device -> {
            Span status = new Span();
            String statusText = getStatusDisplayName(device);
            status.getElement().setAttribute("aria-label", "Status: " + statusText);
            status.getElement().setAttribute("title", "Status: " + statusText);
            status.getElement().getThemeList().add(getStatusTheme(device));
            return status;
        })).setHeader("").setFlexGrow(0).setAutoWidth(true);

        grid.addColumn("trackingNumber").setAutoWidth(true);
        grid.addColumn("region").setAutoWidth(true);
        grid.addColumn("latitude").setAutoWidth(true);
        grid.addColumn("longitude").setAutoWidth(true);
        /*grid.addColumn("temperature").setAutoWidth(true);
        grid.addColumn("conductivity").setAutoWidth(true);
        grid.addColumn("turbidity").setAutoWidth(true);
        grid.addColumn("pH").setAutoWidth(true);*/
        grid.addColumn("installationDate").setAutoWidth(true);

        grid.setItems(query -> sampleDeviceService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)),
                filters).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private void navigateToAddDevice() {
        UI.getCurrent().navigate("ajouter-dispositif");
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    private String getStatusDisplayName(SampleDevice device) {
        DeviceHealth deviceHealth = new DeviceHealth(device);
        DeviceHealth.Status status = deviceHealth.getStatus();


        if (status == DeviceHealth.Status.EXCELLENT) {
            return "Excellent";
        } else if (status == DeviceHealth.Status.FAILING) {
            return "Failing";
        }
        return "Unknown";
    }

    private String getStatusTheme(SampleDevice device) {
        DeviceHealth deviceHealth = new DeviceHealth(device);
        DeviceHealth.Status status = deviceHealth.getStatus();
        String theme = "badge primary small";
        if (status == DeviceHealth.Status.EXCELLENT) {
            theme += " success";
        } else if (status == DeviceHealth.Status.FAILING) {
            theme += " error";
        }
        return theme;
    }
}
