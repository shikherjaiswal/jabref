package org.jabref.gui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import org.jabref.gui.actions.ActionsFX;
import org.jabref.gui.actions.SimpleCommand;

public abstract class SidePaneComponent {

    protected final SidePaneManager manager;
    protected final ToggleCommand toggleCommand;
    private final JabRefIcon icon;
    private final String title;
    private Node contentNode;

    public SidePaneComponent(SidePaneManager manager, JabRefIcon icon, String title) {
        this.manager = manager;
        this.icon = icon;
        this.title = title;
        this.toggleCommand = new ToggleCommand(this);
    }

    protected void hide() {
        manager.hide(this.getType());
    }

    protected void show() {
        manager.show(this.getType());
    }

    private void moveUp() {
        manager.moveUp(this);
    }

    private void moveDown() {
        manager.moveDown(this);
    }

    /**
     * Override this method if the component needs to make any changes before it can close.
     */
    public void beforeClosing() {
        // Nothing to do by default
    }

    /**
     * Override this method if the component needs to do any actions after it is shown.
     */
    public void afterOpening() {
        // Nothing to do by default
    }

    /**
     * Specifies how to this side pane component behaves if there is additional vertical space.
     */
    public abstract Priority getResizePolicy();

    /**
     * @return the command which toggles this {@link SidePaneComponent}
     */
    public ToggleCommand getToggleCommand() {
        return toggleCommand;
    }

    /**
     * @return the action to toggle this {@link SidePaneComponent}
     */
    public abstract ActionsFX getToggleAction();

    /**
     * @return the content of this component
     */
    public final Node getContentPane() {
        if (contentNode == null) {
            contentNode = createContentPane();
        }

        return contentNode;
    }

    /**
     * @return the header pane for this component
     */
    public final Node getHeader() {
        Button close = IconTheme.JabRefIcons.CLOSE.asButton();
        close.setOnAction(event -> hide());

        Button up = IconTheme.JabRefIcons.UP.asButton();
        up.setOnAction(event -> moveUp());

        Button down = IconTheme.JabRefIcons.DOWN.asButton();
        down.setOnAction(event -> moveDown());

        HBox buttonContainer = new HBox();
        buttonContainer.getChildren().addAll(up, down, close);
        BorderPane graphic = new BorderPane();
        graphic.setCenter(icon.getGraphicNode());
        BorderPane container = new BorderPane();
        container.setLeft(graphic);
        container.setCenter(new Label(title));
        container.setRight(buttonContainer);
        container.getStyleClass().add("sidePaneComponentHeader");

        return container;
    }

    /**
     * Create the content of this component
     *
     * @implNote The {@link SidePaneManager} always creates an instance of every side component (e.g., to get the toggle action)
     * but we only want to create the content view if the component is shown to save resources.
     * This is the reason for the lazy loading.
     */
    protected abstract Node createContentPane();

    /**
     * @return the type of this component
     */
    public abstract SidePaneType getType();

    public class ToggleCommand extends SimpleCommand {

        private final SidePaneComponent component;

        public ToggleCommand(SidePaneComponent component) {
            this.component = component;
        }

        @Override
        public void execute() {
            manager.toggle(component.getType());
        }
    }
}