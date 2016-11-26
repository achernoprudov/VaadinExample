package ru.list.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import ru.list.core.data.UserRepository;
import ru.list.core.data.model.User;

/**
 * A simple example to introduce building forms. As your real application is
 * probably much more complicated than this example, you could re-use this form in
 * multiple places. This example component is only used in MainUI.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX. See e.g. AbstractForm in Virin
 * (https://vaadin.com/addon/viritin).
 */
@SpringComponent
@UIScope
class UserEditor extends VerticalLayout {

    private static final Logger log = LoggerFactory.getLogger(UserEditor.class);

    private final UserRepository repository;

    /**
     * The currently edited user
     */
    private User user;

    /* Fields to edit properties in User entity */
    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");

    /* Action buttons */
    private Button save = new Button("Save", FontAwesome.SAVE);
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", FontAwesome.TRASH_O);
    private CssLayout actions = new CssLayout(save, cancel, delete);

    @Autowired
    public UserEditor(UserRepository repository) {
        this.repository = repository;

        addComponents(firstName, lastName, actions);

        // Configure and style components
        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> {
            log.info("Saving user: " + user.toString());
            repository.save(user);
        });
        delete.addClickListener(e -> {
            log.info("Delete user: " + user.toString());
            repository.delete(user);
        });
        cancel.addClickListener(e -> editUser(user));
        setVisible(false);
    }

    interface ChangeHandler {

        void onChange();
    }

    final void editUser(User u) {
        final boolean persisted = u.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            user = repository.findOne(u.getId());
        }
        else {
            user = u;
        }
        cancel.setVisible(persisted);

        // Bind user properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        BeanFieldGroup.bindFieldsUnbuffered(user, this);

        setVisible(true);

        // A hack to ensure the whole form is visible
        save.focus();
        // Select all text in firstName field automatically
        firstName.selectAll();
    }

    void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        save.addClickListener(e -> h.onChange());
        delete.addClickListener(e -> h.onChange());
    }

}