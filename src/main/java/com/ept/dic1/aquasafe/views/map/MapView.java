package com.ept.dic1.aquasafe.views.map;

import com.ept.dic1.aquasafe.data.SampleDevice;
import com.ept.dic1.aquasafe.views.MainLayout;
import com.ept.dic1.aquasafe.views.dispositifs.DeviceService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
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
import com.vaadin.flow.component.map.configuration.style.Fill;
import com.vaadin.flow.component.map.configuration.style.Style;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@PageTitle("Map")
@Route(value = "map", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "USER"})

@CssImport("themes/aquasafe/views/map-view.css")
public class MapView extends HorizontalLayout implements BeforeEnterObserver{

    private List<Location> locations;

    private Map map = new Map();

    private UnorderedList cardList;
    private java.util.Map<Location, Button> locationToCard = new HashMap<>();

    private List<Location> filteredLocations;
    private java.util.Map<Feature, Location> featureToLocation = new HashMap<>();


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        List<String> parameters = queryParameters.getParameters().get("trackingNumber");

        if (parameters != null && !parameters.isEmpty()) {
            String trackingNumber = parameters.iterator().next();

            Location location = locations.stream()
                    .filter(l -> l.getTrackingNumber().equals(trackingNumber))
                    .findFirst()
                    .orElse(null);

            if (location == null) {
                event.rerouteTo("map");
            } else {
                centerMapOn(location);
                scrollToCard(location);
                updateMarkerStyle(location);
            }



            System.out.println("trackingNumber = " + trackingNumber);
            System.out.println("location = " + location);
        }
    }


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

    private Button selectedCardButton = null;
    private void scrollToCard(Location location) {
        if (selectedCardButton != null) {
            selectedCardButton.removeClassName("card-selected");
        }

        Button cardButton = locationToCard.get(location);
        cardButton.addClassName("card-selected");

        selectedCardButton = cardButton;

        cardButton.scrollIntoView();
        cardButton.addClassName("card-selected");
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
             updateMarkerStyle(location);
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
                updateMarkerStyle(location);
                scrollToCard(location);
            });

            Span card = new Span();
            card.addClassNames("card", Width.FULL, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Gap.XSMALL);
            Span region = new Span(location.getRegion());
            Button showInfo = new Button(new Icon(VaadinIcon.ARROW_RIGHT));
            showInfo.addClickListener(e -> {
                UI.getCurrent().navigate("device-dashboard/" + location.getTrackingNumber());
            });
            showInfo.addClassNames(LumoUtility.IconSize.LARGE);

            HorizontalLayout header = new HorizontalLayout(region, showInfo);
            header.setAlignItems(Alignment.BASELINE);

            header.setWidthFull();
            header.setFlexGrow(1, region);

            region.addClassNames(TextColor.SECONDARY);
            Span trackingNumber = new Span(location.getTrackingNumber());
            trackingNumber.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD, TextColor.HEADER, Padding.Bottom.XSMALL);
            Span place = new Span(location.getLatitude() + " " + location.getLongitude());

            place.addClassNames(TextColor.SECONDARY);

            card.add(header, trackingNumber, place);

            button.getElement().appendChild(card.getElement());
            cardList.add(new ListItem(button));
            locationToCard.put(location, button);
        }
    }

    private void updateMarkerStyle(Location location) {
        FeatureLayer featureLayer = this.map.getFeatureLayer();



        for (Feature feature : featureLayer.getFeatures()) {
            com.vaadin.flow.component.map.configuration.style.Icon.Options defaultMarkerIcon = new com.vaadin.flow.component.map.configuration.style.Icon.Options();
            defaultMarkerIcon.setSrc("images/marker.png");

            com.vaadin.flow.component.map.configuration.style.Icon defaultIcon = new com.vaadin.flow.component.map.configuration.style.Icon(defaultMarkerIcon);

            MarkerFeature defaultMarker = (MarkerFeature) feature;
            defaultMarker.setIcon(defaultIcon);

            Location featureLocation = featureToLocation.get(feature);
            if (featureLocation.equals(location)) {
                com.vaadin.flow.component.map.configuration.style.Icon.Options markerIcon = new com.vaadin.flow.component.map.configuration.style.Icon.Options();
                markerIcon.setSrc("images/map-marker.png");

                com.vaadin.flow.component.map.configuration.style.Icon icon = new com.vaadin.flow.component.map.configuration.style.Icon(markerIcon);


                MarkerFeature markerFeature = (MarkerFeature) feature;
                markerFeature.setIcon(icon);

            }
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
            com.vaadin.flow.component.map.configuration.style.Icon.Options defaultMarkerIcon = new com.vaadin.flow.component.map.configuration.style.Icon.Options();
            defaultMarkerIcon.setSrc("images/marker.png");

            com.vaadin.flow.component.map.configuration.style.Icon defaultIcon = new com.vaadin.flow.component.map.configuration.style.Icon(defaultMarkerIcon);


            MarkerFeature feature = new MarkerFeature(new Coordinate(location.getLongitude(), location.getLatitude()), defaultIcon);
            featureToLocation.put(feature, location);
            featureLayer.addFeature(feature);
        });
        updateCardList();
    }

}
