package ru.list.ui;

import com.vaadin.event.SelectionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import ru.list.core.data.UserRepository;
import ru.list.core.data.model.User;

import java.util.List;

@SpringUI
@Theme("valo")
public class MainUI extends UI {

    private final UserRepository repo;

    private final UserEditor editor;

    private final Grid grid;

    private final TextField filter;
    private final Button addNewBtn;
    private final Button showHideActionsBtn;
    private final HorizontalLayout actions;

    @Autowired
    public MainUI(UserRepository repo, UserEditor editor) {
        this.repo = repo;
        this.editor = editor;
        this.grid = new Grid();
        this.filter = new TextField();
        this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
        this.showHideActionsBtn = new Button("Show/hide actions");
        this.actions = new HorizontalLayout(showHideActionsBtn);
    }

    @Override
    protected void init(VaadinRequest request) {
        // build layout
        VerticalLayout mainLayout = new VerticalLayout(actions, grid, editor);
        setContent(mainLayout);

        // Configure layouts and components
        actions.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        grid.setHeight(300, Unit.PIXELS);
        grid.setColumns("id", "firstName", "lastName");

        filter.setInputPrompt("Filter by last name");

        // Hook logic to components

        filter.addTextChangeListener(e -> listCustomers(e.getText()));

        grid.addSelectionListener(this::selectionDidChange);

        showHideActionsBtn.addClickListener(this::showHideButtonDidTap);

        // Instantiate and edit new User the new button is clicked
        addNewBtn.addClickListener(e -> editor.editUser(new User("", "")));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listCustomers(filter.getValue());
        });

        // Initialize listing
        listCustomers(null);
    }

    private void showHideButtonDidTap(Button.ClickEvent clickEvent) {
        if (actions.getComponentCount() == 3) {
            actions.removeComponent(addNewBtn);
            actions.removeComponent(filter);
        } else {
            actions.addComponent(filter);
            actions.addComponent(addNewBtn);
        }
    }


    private void selectionDidChange(SelectionEvent selectionEvent) {
        if (selectionEvent.getSelected().isEmpty()) {
            editor.setVisible(false);
        }
        else {
            editor.editUser((User) grid.getSelectedRow());
        }
    }

    private void listCustomers(String text) {
        List<User> all = StringUtils.isEmpty(text)
                ? repo.findAll()
                : repo.findByLastNameStartsWithIgnoreCase(text);

        BeanItemContainer<User> itemContainer = new BeanItemContainer<>(User.class, all);
        grid.setContainerDataSource(itemContainer);
    }
}