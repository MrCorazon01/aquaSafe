package com.ept.dic1.aquasafe.views.login;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("login-rich-content")
@RolesAllowed({"ADMIN", "USER"})
@CssImport(value = "./themes/aquasafe/views/login-view.css", themeFor = "vaadin-login-form")
public class LoginRichContent extends Div {

    public LoginRichContent() {
        // tag::snippet[]
        // See login-rich-content.css
        addClassName("login-rich-content");

        LoginForm loginForm = new LoginForm();
        loginForm.getElement().getThemeList().add("dark");
        // end::snippet[]
        add(loginForm);
        // Prevent the example from stealing focus when browsing the
        // documentation
        loginForm.getElement().setAttribute("no-autofocus", "");
    }

}
