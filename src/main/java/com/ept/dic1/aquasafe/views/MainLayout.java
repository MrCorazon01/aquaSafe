package com.ept.dic1.aquasafe.views;

import com.ept.dic1.aquasafe.security.SecurityService;
import com.ept.dic1.aquasafe.views.about.AboutView;
import com.ept.dic1.aquasafe.views.dashboard.DashboardView;
import com.ept.dic1.aquasafe.views.dispositifs.DevicesView;
import com.ept.dic1.aquasafe.views.home.HomeView;
import com.ept.dic1.aquasafe.views.map.MapView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private final SecurityService securityService;
    private final transient AuthenticationContext authContext;
    private H2 viewTitle;

    HorizontalLayout header;
    HorizontalLayout logout;

    public MainLayout(@Autowired SecurityService securityService, AuthenticationContext authContext) {
        this.securityService = securityService;
        this.authContext = authContext;

        if (securityService.getAuthenticatedUser() != null) {
            Button logout = new Button("Logout", click ->
                    securityService.logout());
            header = new HorizontalLayout(logout);
        } else {
            header = new HorizontalLayout();
        }

        logout =
                authContext.getAuthenticatedUser(UserDetails.class)
                        .map(user -> {
                            Button logout = new Button("Logout", click ->
                                    this.authContext.logout());
                            Span loggedUser = new Span("Welcome " + user.getUsername());
                            return new HorizontalLayout(loggedUser, logout);
                        }).orElseGet(() -> new HorizontalLayout());

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        HorizontalLayout hLayout = new HorizontalLayout();
        HorizontalLayout title = new HorizontalLayout();
        HorizontalLayout userInfo = new HorizontalLayout();

        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        var bellBtn = new MessagesButton();
        bellBtn.setUnreadMessages(4);

        ContextMenu menu = new ContextMenu();
        menu.setOpenOnClick(true);
        menu.setTarget(bellBtn);
        menu.addItem("Anomalie détectée !");
        menu.addItem("Vérifiez l'état du système pour plus d'informations");
        //menu.addItem("pour plus d'informations");
        menu.addItem("Cliquez ici pour résoudre");



        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        title.add(toggle, viewTitle);
        title.setAlignItems(FlexComponent.Alignment.CENTER);
        title.setSpacing(false);
        userInfo.add(bellBtn, header);
        hLayout.add(title, userInfo);
        hLayout.setFlexGrow(1, title);
        hLayout.setWidth("99%");

        addToNavbar(true, hLayout);
    }

    public class MessagesButton extends Button {

        private final Element numberOfNotifications;

        public MessagesButton() {
            super(VaadinIcon.BELL_O.create());
            numberOfNotifications = new Element("span");
            numberOfNotifications.getStyle()
                    .setPosition(Style.Position.ABSOLUTE)
                    .setTransform("translate(-40%, -85%)");
            numberOfNotifications.getThemeList().addAll(
                    Arrays.asList("badge", "error", "primary", "small", "pill"));
        }

        public void setUnreadMessages(int unread) {
            numberOfNotifications.setText(unread + "");
            if(unread > 0 && numberOfNotifications.getParent() == null) {
                getElement().appendChild(numberOfNotifications);
            } else if(numberOfNotifications.getNode().isAttached()) {
                numberOfNotifications.removeFromParent();
            }
        }

    }

    private void addDrawerContent() {
        H1 appName = new H1("SEN'EAU AquaSafe");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Home", HomeView.class, LineAwesomeIcon.HOME_SOLID.create()));
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, LineAwesomeIcon.CHART_AREA_SOLID.create()));
        nav.addItem(new SideNavItem("Map", MapView.class, LineAwesomeIcon.MAP.create()));
        nav.addItem(
                new SideNavItem("Dispositifs", DevicesView.class, LineAwesomeIcon.NETWORK_WIRED_SOLID.create()));
        nav.addItem(new SideNavItem("About", AboutView.class, LineAwesomeIcon.CENTOS.create()));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
