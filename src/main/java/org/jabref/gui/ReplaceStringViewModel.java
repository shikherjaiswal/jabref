package org.jabref.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.jabref.gui.undo.NamedCompound;
import org.jabref.gui.undo.UndoableFieldChange;
import org.jabref.logic.l10n.Localization;
import org.jabref.model.entry.BibEntry;

public class ReplaceStringViewModel extends AbstractViewModel
{
    private boolean allFieldReplace;
    private String findString;
    private String replaceString;
    private String[] fieldStrings;
    private BasePanel panel;

    private StringProperty findStringProperty = new SimpleStringProperty();
    private StringProperty replaceStringProperty = new SimpleStringProperty();
    private StringProperty fieldStringProperty = new SimpleStringProperty();
    private BooleanProperty allFieldReplaceProperty = new SimpleBooleanProperty();
    private BooleanProperty selectOnlyProperty = new SimpleBooleanProperty();


    public ReplaceStringViewModel(BasePanel basePanel)
    {
        this.panel = basePanel;
    }

    public int replace() {
        findString = findStringProperty.getValue();
        replaceString = replaceStringProperty.getValue();
        fieldStrings = fieldStringProperty.getValue().split(";");
        boolean selOnly = selectOnlyProperty.getValue();
        allFieldReplace = allFieldReplaceProperty.getValue();

        final NamedCompound compound = new NamedCompound(Localization.lang("Replace string"));
        int counter = 0;
        if (this.panel == null)
            return 0;
        if (selOnly) {
            for (BibEntry bibEntry: this.panel.getSelectedEntries())
                counter += replaceItem(bibEntry, compound);
        }
        else {
            for (BibEntry bibEntry: this.panel.getDatabase().getEntries())
                counter += replaceItem(bibEntry, compound);
        }
        return counter;
    }

    /**
     * Does the actual operation on a Bibtex entry based on the
     * settings specified in this same dialog. Returns the number of
     * occurences replaced.
     * Copied and Adapted from org.jabref.gui.ReplaceStringDialog.java
     */
    private int replaceItem(BibEntry entry, NamedCompound compound) {
        int counter = 0;
        if (this.allFieldReplace) {
            for (String fieldName : entry.getFieldNames()) {
                counter += replaceField(entry, fieldName, compound);
            }
        } else {
            for (String espFieldName : fieldStrings) {
                counter += replaceField(entry, espFieldName, compound);
            }
        }
        return counter;
    }

    private int replaceField(BibEntry entry, String fieldname, NamedCompound compound) {
        if (!entry.hasField(fieldname)) {
            return 0;
        }
        String txt = entry.getField(fieldname).get();
        StringBuilder stringBuilder = new StringBuilder();
        int ind;
        int piv = 0;
        int counter = 0;
        int len1 = this.findString.length();
        while ((ind = txt.indexOf(this.findString, piv)) >= 0) {
            counter++;
            stringBuilder.append(txt, piv, ind); // Text leading up to s1
            stringBuilder.append(this.replaceString); // Insert s2
            piv = ind + len1;
        }
        stringBuilder.append(txt.substring(piv));
        String newStr = stringBuilder.toString();
        entry.setField(fieldname, newStr);
        compound.addEdit(new UndoableFieldChange(entry, fieldname, txt, newStr));
        return counter;
    }

    public BooleanProperty allFieldReplaceProperty() {
        return allFieldReplaceProperty;
    }

    public BooleanProperty selectOnlyProperty() {
        return selectOnlyProperty;
    }

    public StringProperty getFieldStringProperty() {
        return fieldStringProperty;
    }

    public StringProperty getFindStringProperty() {
        return findStringProperty;
    }

    public StringProperty getReplaceStringProperty() {
        return replaceStringProperty;
    }
}
