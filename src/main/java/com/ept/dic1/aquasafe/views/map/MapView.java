package com.ept.dic1.aquasafe.views.map;

import com.ept.dic1.aquasafe.data.SampleDevice;
import com.ept.dic1.aquasafe.views.MainLayout;
import com.ept.dic1.aquasafe.views.dispositifs.DeviceService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.Feature;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.layer.FeatureLayer;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;
import com.ept.dic1.aquasafe.utils.Location;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})
public class MapView extends HorizontalLayout {



    private List<Location> locations;

    private Map map = new Map();

    private UnorderedList cardList;
    private java.util.Map<Location, Button> locationToCard = new HashMap<>();

    private List<Location> filteredLocations;
    private java.util.Map<Feature, Location> featureToLocation = new HashMap<>();

    public MapView(DeviceService deviceService) {

        locations = deviceService.getAllDeviceLocations();


        addClassName("map-view");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        map.getElement().setAttribute("theme", "borderless");
        map.getElement().setAttribute("class", "map");
        map.setHeightFull();

        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setSpacing(false);
        sidebar.setPadding(false);

        sidebar.setWidth("auto");
        sidebar.addClassNames("sidebar");
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search");
        searchField.setWidthFull();
        searchField.addClassNames(Padding.MEDIUM, BoxSizing.BORDER);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> {
            updateFilter(searchField.getValue().toLowerCase());
        });
        searchField.setClearButtonVisible(true);
        searchField.setSuffixComponent(new Icon("lumo", "search"));

        Scroller scroller = new Scroller();
        scroller.addClassNames(Padding.Horizontal.MEDIUM, Width.FULL, BoxSizing.BORDER);

        cardList = new UnorderedList();
        cardList.addClassNames("card-list", Gap.XSMALL, Display.FLEX, FlexDirection.COLUMN, ListStyleType.NONE,
                Margin.NONE, Padding.NONE);
        sidebar.add(searchField, scroller);
        scroller.setContent(cardList);

        add(map, sidebar);

        centerMapDefault();
        configureMap();
        updateCardList();
    }

    private void centerMapOn(Location location) {
        View view = map.getView();
        view.setCenter(new Coordinate(location.getLongitude(), location.getLatitude()));
        view.setZoom(14);
    }

    private void scrollToCard(Location location) {
        locationToCard.get(location).scrollIntoView();
    }

    private void centerMapDefault() {
        View view = new View();
        view.setCenter(new Coordinate(-14.5, 14.5));
        view.setZoom(7.4f);
        map.setView(view);
    }

    private void configureMap() {

        this.centerMapDefault();

        this.map.addFeatureClickListener(e -> {
            Feature feature = e.getFeature();
            Location location = featureToLocation.get(feature);
            this.centerMapOn(location);
            this.scrollToCard(location);
        });

        this.updateFilter("");
    }

    private void updateCardList() {
        cardList.removeAll();
        locationToCard.clear();
        for (Location location : filteredLocations) {
            Button button = new Button();
            button.addClassNames(Height.AUTO, Padding.MEDIUM);
            button.addClickListener(e -> {
                centerMapOn(location);
            });

            Span card = new Span();
            card.addClassNames("card", Width.FULL, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Gap.XSMALL);
            Span country = new Span(location.getRegion());
            country.addClassNames(TextColor.SECONDARY);
            Span city = new Span(location.getTrackingNumber());
            city.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD, TextColor.HEADER, Padding.Bottom.XSMALL);
            Span place = new Span(location.getRegion());
            place.addClassNames(TextColor.SECONDARY);

            card.add(country, city, place);

            button.getElement().appendChild(card.getElement());
            cardList.add(new ListItem(button));
            locationToCard.put(location, button);
        }
    }

    private void updateFilter(String filter) {
        featureToLocation.clear();

        filteredLocations = locations.stream()
                .filter(location -> location.getTrackingNumber().toLowerCase().contains(filter)
                        || location.getRegion().toLowerCase().contains(filter))
                .collect(Collectors.toList());

        FeatureLayer featureLayer = this.map.getFeatureLayer();

        for (Feature f : featureLayer.getFeatures().toArray(Feature[]::new)) {
            featureLayer.removeFeature(f);
        }

        this.filteredLocations.forEach((location) -> {

            MarkerFeature feature = new MarkerFeature(new Coordinate(location.getLongitude(), location.getLatitude()));
            featureToLocation.put(feature, location);
            featureLayer.addFeature(feature);
        });
        updateCardList();
    }

}
