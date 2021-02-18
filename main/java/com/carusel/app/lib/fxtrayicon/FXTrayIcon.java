package com.carusel.app.lib.fxtrayicon;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for creating a JavaFX System Tray Icon.
 * Uses JavaFX controls to create the icon.
 * Allows for a developer to create a tray icon
 * using JavaFX code, without having to access
 * the AWT API.
 */
@SuppressWarnings("unused")
public class FXTrayIcon{
    public interface OnDoubleClickTrayIconListener{
        void callback();
    }
    private final List<OnDoubleClickTrayIconListener> onDoubleClickTrayIconListeners = new ArrayList<>();
    public void registerListener(OnDoubleClickTrayIconListener listener){
        onDoubleClickTrayIconListeners.add(listener);
    }

    private final SystemTray tray;
    private final Stage parentStage;
    private final TrayIcon trayIcon;
    private boolean showing;
    private final PopupMenu popupMenu = new PopupMenu();

    public FXTrayIcon(Stage parentStage, URL iconImagePath){
        if(!SystemTray.isSupported()){
            throw new UnsupportedOperationException("SystemTray icons are not " +
                    "supported by the current desktop environment.");
        }else{
            tray = SystemTray.getSystemTray();
        }

        // Keeps the JVM running even if there are no
        // visible JavaFX Stages
        Platform.setImplicitExit(false);

        // Set the SystemLookAndFeel, if not available, use default
        // User could change this by calling UIManager.setLookAndFeel themselves
        // after instantiating the FXTrayIcon
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ignored){
        }

        try{
            final Image iconImage = ImageIO.read(iconImagePath)
                    // Some OSes do not behave well if the icon is larger than 16x16
                    // Image.SCALE_SMOOTH will provide the best quality icon in most instances
                    .getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            this.parentStage = parentStage;
            this.trayIcon = new TrayIcon(iconImage, parentStage.getTitle(), popupMenu);
        }catch(IOException e){
            throw new RuntimeException("Unable to read the Image at the provided path.");
        }
    }

    /**
     * Adds the FXTrayIcon to the system tray.
     * This will add the TrayIcon with the image initialized in the
     * {@code FXTrayIcon}'s constructor. By default, an empty popup
     * menu is shown.
     * By default, {@code javafx.application.Platform.setImplicitExit(false)}
     * will be called. This will allow the application to continue running
     * and show the tray icon after no more JavaFX Stages are visible. If
     * this is not the behavior that you intend, call {@code setImplicitExit}
     * to true after calling {@code show()}.
     */
    public void show(){
        SwingUtilities.invokeLater(() -> {
            try{
                tray.add(this.trayIcon);
                this.showing = true;

                // Show parent stage when user double-clicks the icon
                this.trayIcon.addActionListener(e -> {
                    if(this.parentStage != null){
                        Platform.runLater(() -> {
                            parentStage.show();
                            onDoubleClickTrayIconListeners
                                    .forEach(OnDoubleClickTrayIconListener::callback);
                        });
                    }
                });
            }catch(AWTException e){
                throw new RuntimeException("Unable to add TrayIcon", e);
            }
        });
    }

    /**
     * Removes the MenuItem at the given index
     *
     * @param index Index of the MenuItem to remove
     */
    public void removeMenuItem(int index){
        EventQueue.invokeLater(() -> this.popupMenu.remove(index));
    }

    /**
     * Removes the specified item from the FXTrayIcon's menu. Does nothing
     * if the item is not in the menu.
     *
     * @param fxMenuItem The JavaFX MenuItem to remove from the menu.
     */
    public void removeMenuItem(javafx.scene.control.MenuItem fxMenuItem){
        EventQueue.invokeLater(() -> {
            MenuItem toBeRemoved = null;
            for(int i = 0; i < this.popupMenu.getItemCount(); i++){
                MenuItem awtItem = this.popupMenu.getItem(i);
                if(awtItem.getLabel().equals(fxMenuItem.getText()) ||
                        awtItem.getName().equals(fxMenuItem.getText())){
                    toBeRemoved = awtItem;
                }
            }
            if(toBeRemoved != null){
                this.popupMenu.remove(toBeRemoved);
            }
        });
    }

    /**
     * Adds a separator line to the Menu at the current position.
     */
    public void addSeparator(){
        EventQueue.invokeLater(this.popupMenu::addSeparator);
    }

    /**
     * Adds a separator line to the Menu at the given position.
     *
     * @param index The position at which to add the separator
     */
    public void insertSeparator(int index){
        EventQueue.invokeLater(() -> this.popupMenu.insertSeparator(index));
    }

    /**
     * Adds the specified MenuItem to the FXTrayIcon's menu
     *
     * @param menuItem MenuItem to be added
     */
    public void addMenuItem(javafx.scene.control.MenuItem menuItem){
        if(menuItem instanceof Menu){
            addMenu((Menu) menuItem);
            return;
        }
        if(!isUnique(menuItem)){
            throw new UnsupportedOperationException("Menu Item labels must be unique.");
        }
        EventQueue.invokeLater(() -> this.popupMenu.add(AWTUtils.convertFromJavaFX(menuItem)));
    }

    private void addMenu(Menu menu){
        EventQueue.invokeLater(() -> {
            java.awt.Menu awtMenu = new java.awt.Menu(menu.getText());
            menu.getItems().forEach(subItem -> awtMenu.add(AWTUtils.convertFromJavaFX(subItem)));
            this.popupMenu.add(awtMenu);
        });
    }

    /**
     * Returns the MenuItem at the given index. The MenuItem
     * returned is the AWT MenuItem, and not the JavaFX MenuItem,
     * thus this should only be called when extending the functionality
     * of the AWT MenuItem.
     * <p>
     * NOTE: This should be called via the
     * {@code EventQueue.invokeLater()} method as well as any
     * subsequent operations on the MenuItem that is returned.
     *
     * @param index Index of the MenuItem to be returned.
     * @return The MenuItem at {@code index}
     */
    public MenuItem getMenuItem(int index){
        return this.popupMenu.getItem(index);
    }

    /**
     * Sets the FXTrayIcon's tooltip that is displayed on mouse hover.
     *
     * @param tooltip The text of the tooltip
     */
    public void setTrayIconTooltip(String tooltip){
        EventQueue.invokeLater(() -> this.trayIcon.setToolTip(tooltip));
    }

    /**
     * Removes the {@code FXTrayIcon} from the system tray.
     * Also calls {@code Platform.setImplicitExit(true)}, thereby
     * allowing the JVM to terminate after the last JavaFX {@code Stage}
     * is hidden.
     */
    public void hide(){
        EventQueue.invokeLater(() -> {
            tray.remove(trayIcon);
            this.showing = false;
            Platform.setImplicitExit(true);
        });
    }

    /**
     * Returns true if the SystemTray icon is visible.
     *
     * @return true if the SystemTray icon is visible.
     */
    public boolean isShowing(){
        return this.showing;
    }

    private boolean isUnique(javafx.scene.control.MenuItem fxItem){
        if(this.popupMenu.getItemCount() == 0){
            return true;
        }
        for(int i = 0; i < this.popupMenu.getItemCount(); i++){
            if(this.popupMenu.getItem(i).getName().equals(fxItem.getText())){
                return false;
            }
        }
        return true;
    }

}