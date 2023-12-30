package com.ept.dic1.aquasafe.views.dispositifs;

import com.ept.dic1.aquasafe.data.SampleDevice;
import com.ept.dic1.aquasafe.services.SampleDeviceService;
import com.ept.dic1.aquasafe.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Ajouter Dispositif")
@Route(value = "ajouter-dispositif", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})
public class AddDeviceView extends Composite<VerticalLayout> {

    private final SampleDeviceService sampleDeviceService;

    public AddDeviceView(SampleDeviceService sampleDeviceService) {
        this.sampleDeviceService = sampleDeviceService;

        createForm();
    }

    private void createForm(){
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3();
        TextField trackingNumberField = new TextField();
        FormLayout formLayout2Col = new FormLayout();
        TextField latitudeField = new TextField();
        TextField longitudeField = new TextField();
        Select<String> regionSelect = new Select<>();
        DatePicker installationDatePicker = new DatePicker();
        HorizontalLayout layoutRow = new HorizontalLayout();
        Button saveButton = new Button();
        Button cancelButton = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth("100%");

        layoutColumn2.setHeight("min-content");
        h3.setText("Ajouter un dispositif");
        h3.setWidth("100%");
        trackingNumberField.setLabel("Tracking Number");
        trackingNumberField.setWidth("100%");
        formLayout2Col.setWidth("100%");
        latitudeField.setLabel("Latitude");
        latitudeField.setWidth("min-content");
        longitudeField.setLabel("Longitude");
        longitudeField.setWidth("min-content");
        regionSelect.setLabel("Region");
        regionSelect.setWidth("min-content");
        setRegionData(regionSelect);
        installationDatePicker.setLabel("Installation Date");
        installationDatePicker.setWidth("min-content");
        layoutRow.setWidthFull();
        layoutColumn2.setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("770px");
        layoutRow.setHeight("min-content");
        saveButton.setText("Enregistrer");
        saveButton.setWidth("min-content");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> {
            SampleDevice newDevice = new SampleDevice();
            newDevice.setTrackingNumber(trackingNumberField.getValue());
            newDevice.setLatitude(Double.parseDouble(latitudeField.getValue()));
            newDevice.setLongitude(Double.parseDouble(longitudeField.getValue()));
            newDevice.setRegion(regionSelect.getValue());
            // Example: Set installation date if selected
            if (installationDatePicker.getValue() != null) {
                newDevice.setInstallationDate(installationDatePicker.getValue());
            }

            // Save device to database
            sampleDeviceService.save(newDevice);
            // Show success notification
            showSuccessNotification();

            // Redirect to the devices view
            navigateToDevicesView();
        });

        cancelButton.setText("Annuler");
        cancelButton.setWidth("min-content");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3);
        layoutColumn2.add(trackingNumberField);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(latitudeField);
        formLayout2Col.add(longitudeField);
        formLayout2Col.add(regionSelect);
        formLayout2Col.add(installationDatePicker);
        layoutColumn2.add(layoutRow);
        layoutRow.add(saveButton);
        layoutRow.add(cancelButton);
    }

    private void setRegionData(Select<String> select) {
        List<String> senegalRegions = List.of(
                "Dakar", "Thiès", "Diourbel", "Louga", "Fatick", "Saint-Louis", "Kaolack", "Kaffrine",
                "Tambacounda", "Kolda", "Ziguinchor", "Sédhiou", "Matam"
        );
        select.setItems(senegalRegions);
    }

    private void showSuccessNotification() {
        Notification successNotification = Notification.show("Dispositif enregistré avec succès", 5000, Notification.Position.TOP_CENTER);
        successNotification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }


    private void navigateToDevicesView() {
        UI.getCurrent().navigate("dispositifs");
    }

}