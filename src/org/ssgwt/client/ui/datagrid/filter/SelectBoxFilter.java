/**
 * Copyright 2012 A24Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ssgwt.client.ui.datagrid.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * The drop down list filter that can be used on the SSDataGrid with a FilterSortHeader
 *
 * @author Michael Barnard <michael.barnard@a24group.com>
 * @since  11 January 2012
 */
public class SelectBoxFilter extends AbstractHeaderFilter {

    /**
     * Instance of the UiBinder
     */
    private static Binder uiBinder = GWT.create(Binder.class);

    /**
     * Holds an instance of the default resources
     */
    private static SelectBoxFilterResources DEFAULT_RESOURCES;

    /**
     * Holds an instance of resources
     */
    private final SelectBoxFilterResources resources;

    /**
     * The filter container
     */
    @UiField
    FocusPanel filterContainer;

    /**
     * The title label
     */
    @UiField
    Label titleLabel;

    /**
     * The check box on the filter popup
     */
    @UiField
    CheckBox checkBox;

    /**
     * The label of the check box
     */
    @UiField
    Label checkBoxLabel;

    /**
     * The label of the drop down list box
     */
    @UiField
    Label listBoxLabel;

    /**
     * The drop down list box
     */
    @UiField(provided = true)
    ListBox listBox;

    /**
     * The icon that clears a the filter criteria
     */
    @UiField
    Image removeFilterIcon;

    /**
     * The apply button
     */
    @UiField
    Button applyButton;

    /**
     * Holds the index that was previously selected in the list box
     */
    int previousIndex;

    /**
     * Whether the list values should include an empty value
     */
    boolean includeEmptyValue = true;

    /**
     * Stores the values that will be used in the drop down list
     * One empty value will always be present if includeEmptyValue is true
     */
    String[] values = new String[]{""};

    /**
     * Stores the key used for empty items
     */
    String sEmptyKey = "";

    /**
     * Stores the values that will be used in the drop down list
     * One empty value will always be present if includeEmptyValue is true
     * This is a key value pair for referencing by id
     */
    HashMap<String, String> valueMap = new HashMap<String, String>();

    /**
     * Flag to indicate whether or not a advanced list will be used
     */
    private boolean bIsAdvancedMap = false;

    /**
     * UiBinder interface for the composite
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    interface Binder extends UiBinder<Widget, SelectBoxFilter> {
    }

    /**
     * The resources interface for the select box filter
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    public interface SelectBoxFilterResources extends Resources {

        /**
         * Retrieves an implementation of the Style interface generated using the specified css file
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return An implementation of the Style interface
         */
        @Source("SelectBoxFilter.css")
        Style selectBoxFilterStyle();
    }

    /**
     * The css resource for the select box filter
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    public interface Style extends CssResource {

        /**
         * The style for the panel that contains all the elements on the select box filter
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String listBoxFilterStyle();

        /**
         * The style for the speech bubble arrow border
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String arrowBorderStyle();

        /**
         * The style for the speech bubble arrow
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String arrowStyle();

        /**
         * The style for the title
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String titleStyle();

        /**
         * The style the sets the position of the remove filter icon
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String removeFilterIconStyle();

        /**
         * The style for the container that holds the checkbox
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String checkBoxContainerStyle();

        /**
         * The style for the checkbox
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String checkBoxStyle();

        /**
         * The style for the label of the checkbox
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String checkBoxLabelStyle();

        /**
         * The style for the select box label
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String listBoxLabelStyle();

        /**
         * The style for the container that holds the select box
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String listBoxStyleContainer();

        /**
         * The style for the select box
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String listBoxStyle();

       /**
        * The style for the multiselect box
        *
        * @author Alec Erasmus <alec.erasmus@a24group.com>
        * @since  25 April 2013
        *
        * @return The name of the compiled style
        */
       String multiListBoxStyle();

        /**
         * The style for the container that holds the apply button
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String applyButtonContainer();

        /**
         * The style for the apply button
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String applyButton();

        /**
         * The style for the apply button when it is selected
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The name of the compiled style
         */
        String applyButtonDown();
    }

    /**
     * The criteria object that will be used to represent the data enter by the user on the select filter
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    public static class SelectBoxFilterCriteria extends Criteria {

        /**
         * The criteria the user entered on the text filter
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         */
        private String criteria;

        /**
         * Flag to indicate if the user is looking for empty entries only
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         */
        private boolean findEmptyEntriesOnly;

        /**
         * Retrieve the flag that indicates if the user is looking for empty entries only
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The flag that indicates if the user is looking for empty entries only
         */
        public boolean isFindEmptyEntriesOnly() {
            return findEmptyEntriesOnly;
        }

        /**
         * Sets the flag that the user is looking for empty entries only or not
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @param findEmptyEntriesOnly - The new value for the flag value
         */
        public void setFindEmptyEntriesOnly(boolean findEmptyEntriesOnly) {
            this.findEmptyEntriesOnly = findEmptyEntriesOnly;
        }

        /**
         * Retrieves the criteria the user entered on the text filter
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @return The criteria the user entered on the text filter
         */
        public String getCriteria() {
            return criteria;
        }

        /**
         * Sets the criteria the user entered on the text filter
         *
         * @author Michael Barnard <michael.barnard@a24group.com>
         * @since  11 January 2012
         *
         * @param criteria - The criteria the user entered on the text filter
         */
        public void setCriteria(String criteria) {
            this.criteria = criteria;
        }
    }

    /**
     * Create an instance on the default resources object if it the
     * DEFAULT_RESOURCES variable is null if not it just return the object in
     * the DEFAULT_RESOURCES variable
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @return the default resource object
     */
    private static SelectBoxFilterResources getDefaultResources() {
        if (DEFAULT_RESOURCES == null) {
            DEFAULT_RESOURCES = GWT.create(SelectBoxFilterResources.class);
        }
        return DEFAULT_RESOURCES;
    }

    /**
     * Class constructor that uses the default resources class
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    public SelectBoxFilter() {
        this(getDefaultResources(), false);
    }

    /**
     * Class constructor that uses the default resources class
     *
     * @author Alec Erasmus <alec.erasmus@a24group.com>
     * @since  26 April 2013
     *
     * @param multiselect - Specifies if multiple selection is enabled
     */
    public SelectBoxFilter(boolean multiselect) {
        this(getDefaultResources(), multiselect);
    }

    /**
     * Class constructor that takes a custom resources class
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @param resources - The resources the select box filter should use
     * @param multiselect - Specifies if multiple selection is enabled
     */
    public SelectBoxFilter(SelectBoxFilterResources resources, boolean multiselect) {
        super(true);
        this.resources = resources;
        this.resources.selectBoxFilterStyle().ensureInjected();
        this.setStyleName("");
        this.listBox = new ListBox(multiselect);
        this.setWidget(uiBinder.createAndBindUi(this));
        if (multiselect) {
            this.listBox.setStyleName(resources.selectBoxFilterStyle().multiListBoxStyle());
        } else {
            this.listBox.setStyleName(resources.selectBoxFilterStyle().listBoxStyle());
        }
        setCriteria(new SelectBoxFilterCriteria());
        addKeyEventHandlers();
        addRemoveIconEventHandlers();
        addApplyButtonEventHandlers();
        addCheckBoxEventHandlers();
    }

    /**
     * This will add event handlers for key events on the filter widget
     *
     * @author Ruan Naude <nauderuan777@gmail.com>
     * @since 27 May 2013
     */
    private void addKeyEventHandlers() {
        filterContainer.addKeyUpHandler(new KeyUpHandler() {

            /**
             * This will handle the key up events on the input
             *
             * @param event The key up event
             *
             * @author Ruan Naude <nauderuan777@gmail.com>
             * @since 27 May 2013
             */
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getSource() == filterContainer &&
                    event.getNativeKeyCode() == KeyCodes.KEY_ENTER
                ) {
                    applyButton.removeStyleName(resources.selectBoxFilterStyle().applyButtonDown());
                    closeFilterPopup(false);
                }
            }
        });
    }

    /**
     * Sets the title of the Filter popup
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @param title - The title that should be displayed on the popup
     */
    @Override
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /**
     * Retrieves the criteria of the filter
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @return The criteria of the obejct
     */
    @Override
    public SelectBoxFilterCriteria getCriteria() {
        return (SelectBoxFilterCriteria)this.filterCirteria;
    }

    /**
     * Function that check if the filter is active by checking the value on the criteria object
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @return Flag to indicate if the filter should be active based on the data in the criteria object
     */
    @Override
    protected boolean checkFilterActive() {
        if (getCriteria().isFindEmptyEntriesOnly()) {
            return true;
        } else if (getCriteria().getCriteria() != null && !getCriteria().getCriteria().trim().equals(sEmptyKey)) {
            return true;
        }
        return false;
    }

    /**
     * Function to update the criteria with data in the fields
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    @Override
    protected void updateCriteriaObject() {
        getCriteria().setFindEmptyEntriesOnly(checkBox.getValue());
        if (bIsAdvancedMap){
            if (listBox.isMultipleSelect()) {
                getCriteria().setCriteria(getMultiselectListBoxAdvancedSelectedItems());
            } else {
                getCriteria().setCriteria(getKeyFromValueMap(listBox.getItemText(listBox.getSelectedIndex())));
            }
        } else {
            if (listBox.isMultipleSelect()) {
                getCriteria().setCriteria(getMultiselectListBoxSelectedItems());
            } else {
                getCriteria().setCriteria(values[listBox.getSelectedIndex()]);
            }
        }
    }

    /**
     * Get the values selected in a multiselect drop down and concatenate the string with a "," separating the values
     * for the advanced map.
     *
     * @author Alec Erasmus <alec.erasmus@a24group.com>
     * @since  26 April 2013
     *
     * @return the selected items separated with a ","
     */
    public String getMultiselectListBoxAdvancedSelectedItems() {
        String selectedItem = null;
        for (int i = 0; i < listBox.getItemCount(); i++) {
            if (listBox.isItemSelected(i)) {
                if (selectedItem == null) {
                    selectedItem = getKeyFromValueMap(listBox.getItemText(i));
                } else {
                    selectedItem = selectedItem + "," + getKeyFromValueMap(listBox.getItemText(i));
                }
             }
        }
        return selectedItem;
    }

    /**
     * Get the values selected in a multiselect drop down and concatenate the string with a "," separating the values
     *
     * @author Alec Erasmus <alec.erasmus@a24group.com>
     * @since  26 April 2013
     *
     * @return the selected items separated with a ","
     */
    public String getMultiselectListBoxSelectedItems() {
        String selectedItem = null;
        for (int i = 0; i < listBox.getItemCount(); i++) {
            if (listBox.isItemSelected(i)) {
                if (selectedItem == null) {
                    selectedItem = values[i];
                } else {
                    selectedItem = selectedItem + "," + values[i];
                }
             }
        }
        return selectedItem;
    }

    /**
     * Function that sets the criteria object to a state where the filter will be inactive
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    @Override
    protected void setCriteriaObjectEmpty() {
        getCriteria().setFindEmptyEntriesOnly(false);
        getCriteria().setCriteria("");
    }

    /**
     * Function used to update the input fields
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    @Override
    protected void updateFieldData() {
        checkBox.setValue(getCriteria().isFindEmptyEntriesOnly());
        
        if (isMultiSelect()) {
            setMultiSelectListBoxValues();
        } else {
            setSingleSelectListBoxValue();
        }
        
        listBox.setEnabled(!checkBox.getValue());
    }
    
    /**
     * This function will set the selected items of the listbox on the filter
     * 
     * @author Ruan Naude <nauderuan777@gmail.com>
     * @since  09 July 2013
     */
    private void setMultiSelectListBoxValues() {
        ArrayList<Integer> selectedIndexes = new ArrayList<Integer>();
        String[] selectedValues = null;
        
        //if the criteria is empty then set first item selected if one exist
        if (getCriteria().getCriteria() != null && !getCriteria().getCriteria().trim().equals("")) {
            
            //spilt the criteria to get all selected values
            selectedValues = getCriteria().getCriteria().split(",");
            
            //add selected value indexes to the array
            for (String value : selectedValues) {
                if (bIsAdvancedMap) {
                    selectedIndexes.add(findAdvancedIndexValue(value));
                } else {
                    selectedIndexes.add(findIndexOf(value));
                }
            }
            
            //set each index in the array selected in the list box
            if (selectedIndexes.isEmpty()) {
                if (listBox.getItemCount() > 0) {
                    listBox.setSelectedIndex(0);
                }
            } else {
                for (Integer integer : selectedIndexes) {
                    if (listBox.getItemCount() > 0) {
                        listBox.setItemSelected(integer, true);
                    }
                }
            }
        } else {
            if (listBox.getItemCount() > 0) {
                listBox.setSelectedIndex(0);
            }
        }
    }
    
    /**
     * This function will set the selected item of the listbox on the filter
     * 
     * @author Ruan Naude <nauderuan777@gmail.com>
     * @since  09 July 2013
     */
    private void setSingleSelectListBoxValue() {
        int index = 0;
        if (bIsAdvancedMap) {
            index = findAdvancedIndexValue(getCriteria().getCriteria());
        } else {
            index = findIndexOf(getCriteria().getCriteria());
        }
        if (index != -1) {
            listBox.setSelectedIndex(index);
        } else {
            listBox.setSelectedIndex(0);
        }
    }

    /**
     * Clear all the ui fields to their default states
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    @Override
    protected void clearFields() {
        checkBox.setValue(false);
        listBox.setSelectedIndex(0);
        listBox.setEnabled(true);
    }

    /**
     * Adds event handlers to the checkbox on the TextFilter
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    private void addCheckBoxEventHandlers() {
        this.checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            /**
             * The function that will be called if the value on the check box changes
             *
             * @author Michael Barnard <michael.barnard@a24group.com>
             * @since  11 January 2012
             *
             * @param event The event that should be handled
             */
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue()) {
                    listBox.setEnabled(false);
                    previousIndex = listBox.getSelectedIndex();
                    listBox.setSelectedIndex(0);
                } else {
                    listBox.setEnabled(true);
                    listBox.setSelectedIndex(previousIndex);
                }
            }
        });
    }

    /**
     * Function that adds the required events handlers to the apply button
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    private void addApplyButtonEventHandlers() {
        this.applyButton.addMouseDownHandler(new MouseDownHandler() {

            /**
             * The event that is call if the apply button fires a mouse down event
             *
             * @author Michael Barnard <michael.barnard@a24group.com>
             * @since  11 January 2012
             *
             * @param event - The mouse down event that should be handled
             */
            @Override
            public void onMouseDown(MouseDownEvent event) {
                applyButton.addStyleName(resources.selectBoxFilterStyle().applyButtonDown());
            }
        });

        this.applyButton.addMouseUpHandler(new MouseUpHandler() {

            /**
             * The event that is call if the apply button fires a mouse up event
             *
             * @author Michael Barnard <michael.barnard@a24group.com>
             * @since  11 January 2012
             *
             * @param event - The mouse up event that should be handled
             */
            @Override
            public void onMouseUp(MouseUpEvent event) {
                applyButton.removeStyleName(resources.selectBoxFilterStyle().applyButtonDown());
                closeFilterPopup(false);
            }
        });

        this.applyButton.addMouseOutHandler(new MouseOutHandler() {

            /**
             * The event that is call if the apply button fires a mouse out event
             *
             * @author Michael Barnard <michael.barnard@a24group.com>
             * @since  11 January 2012
             *
             * @param event - The mouse out event that should be handled
             */
            @Override
            public void onMouseOut(MouseOutEvent event) {
                applyButton.removeStyleName(resources.selectBoxFilterStyle().applyButtonDown());
            }
        });
    }

    /**
     * Adds the required events handlers to the remove filter icon
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     */
    private void addRemoveIconEventHandlers() {
        this.removeFilterIcon.addMouseOverHandler(new MouseOverHandler() {

            /**
             * The event that is call if the remove criteria icon fires a mouse over event
             *
             * @author Michael Barnard <michael.barnard@a24group.com>
             * @since  11 January 2012
             *
             * @param event - The mouse over event that should be handled
             */
            @Override
            public void onMouseOver(MouseOverEvent event) {
                removeFilterIcon.setResource(resources.removeFilterIconOver());
            }
        });

        this.removeFilterIcon.addMouseOutHandler(new MouseOutHandler() {

            /**
             * The event that is call if the remove criteria icon fires a mouse out event
             *
             * @author Michael Barnard <michael.barnard@a24group.com>
             * @since  11 January 2012
             *
             * @param event - The mouse out event that should be handled
             */
            @Override
            public void onMouseOut(MouseOutEvent event) {
                removeFilterIcon.setResource(resources.removeFilterIconUp());
            }
        });

        this.removeFilterIcon.addMouseDownHandler(new MouseDownHandler() {

            /**
             * The event that is call if the remove criteria icon fires a mouse down event
             *
             * @author Michael Barnard <michael.barnard@a24group.com>
             * @since  11 January 2012
             *
             * @param event - The mouse down event that should be handled
             */
            @Override
            public void onMouseDown(MouseDownEvent event) {
                removeFilterIcon.setResource(resources.removeFilterIconDown());
            }
        });

        this.removeFilterIcon.addMouseUpHandler(new MouseUpHandler() {

            /**
             * The event that is call if the remove criteria icon fires a mouse up event
             *
             * @author Michael Barnard <michael.barnard@a24group.com>
             * @since  11 January 2012
             *
             * @param event - The mouse up event that should be handled
             */
            @Override
            public void onMouseUp(MouseUpEvent event) {
                removeFilterIcon.setResource(resources.removeFilterIconOver());
                clearFields();
            }
        });
    }

    /**
     * Used to set the list in the filter drop down list
     * Will add an empty item if specified
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @param listItems a list of items to set as the list
     */
    public void setListBoxData(List<String> listItems) {
        String[] items = new String[0];
        setListBoxData(listItems.toArray(items));
    }

    /**
     * Used to set the list in the filter drop down list
     * Will add an empty item if specified
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @param listItems a hashmap of items to set as the list
     */
    public void setListBoxData(HashMap<String, String> listItems) {
        emptyListBox();
        boolean emptyInclude = false;
        if (listItems.size() == 0){
            valueMap.put(sEmptyKey, "");
        } else {
            if (includeEmptyValue) {
                valueMap.putAll(listItems);
                emptyInclude = true;
            } else {
                valueMap.putAll(listItems);
                emptyInclude = false;
            }
        }
        previousIndex = 0;
        if (emptyInclude) {
            listBox.addItem("", sEmptyKey);
        }
        Map<String, String> map = valueMap;
        Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            listBox.addItem(entry.getValue(), entry.getKey());
        }
        if (emptyInclude) {
            valueMap.put(sEmptyKey, "");
        }
        bIsAdvancedMap = true;
        updateFieldData();
    }

    /**
     * Used to set the list in the filter drop down list
     * Will add an empty item if specified
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @param listItems an array of items to set as the list
     */
    public void setListBoxData(String[] listItems) {
        emptyListBox();
        if (listItems.length == 0){
            values = new String[]{""};
        } else {
            if (includeEmptyValue) {
                String[] joinedArray = new String[listItems.length + 1];
                joinedArray[0] = "";
                for (int x = 1; x < joinedArray.length; x++) {
                    joinedArray[x] = listItems[x - 1];
                }
                values = joinedArray;
            } else {
                values = listItems;
            }
        }
        previousIndex = 0;
        for (int x = 0; x < this.values.length; x++) {
            listBox.addItem(this.values[x]);
        }
        bIsAdvancedMap = false;
        updateFieldData();
    }

    /**
     * Gets the value from the dropdown list in the filter item
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @return an array of the values in the dropdown list
     */
    public String[] getListBoxData() {
        return this.values;
    }

    /**
     * Sets the toggle for including an empty field for the filter
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @param includeEmptyValue Whether to include
     */
    public void setIncludeEmptyToggle(boolean includeEmptyValue) {
        this.includeEmptyValue = includeEmptyValue;
    }

    /**
     * Retrieves the check box that is displayed on the TextFilter.
     * This function is protected as it is only used by test cases.
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @return instance of the check box that is displayed on the TextFilter
     */
    protected CheckBox getCheckBox() {
        return checkBox;
    }

    /**
     * Retrieves the text box that is displayed on the TextFilter.
     * This function is protected as it is only used by test cases.
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @return instance of the text box that is displayed on the TextFilter
     */
    protected ListBox getListBox() {
        return listBox;
    }

    /**
     * Retrieves the title label that is displayed on the TextFilter.
     * This function is protected as it is only used by test cases.
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @return instance of the title label that is displayed on the TextFilter
     */
    protected Label getTitleLabel() {
        return titleLabel;
    }

    /**
     * Sets the text for the listBoxLabel
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  14 January 2012
     *
     * @param labelText - The string to set on the listbox label
     */
    public void setListBoxLabelText(String labelText){
        this.listBoxLabel.setText(labelText);
    }

    /**
     * Gets the text currently selected in the dropdown list
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @return A string of the current selectbox value
     */
    public String getSelectBoxItemText(){
        return this.values[listBox.getSelectedIndex()];
    }

    /**
     * Gets the index of the string that is passed in. Returns -1 if string is not found
     *
     * @param item The item to search for in the list
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  11 January 2012
     *
     * @return the index of the current item passed in
     */
    public int findIndexOf(String item) {
        if (item == null) {
            getCriteria().setCriteria(sEmptyKey);
            item = "";
        }
        for (int x = 0; x < values.length; x++) {
            if (item.equals(values[x])) {
                return x;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the value for the advanced list key
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  16 January 2013
     *
     * @param item The key of the item to search for in the list
     *
     * @return the index of the current item's key passed in
     */
    public int findAdvancedIndexValue(String item) {
        Map<String, String> map = valueMap;
        Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            if (item.equals((entry.getKey()))) {
                item = entry.getValue();
                int listCount = listBox.getItemCount();
                for (int x = 0; x < listCount; x++) {
                    if (item.equals(listBox.getItemText(x))) {
                        return x;
                    }
                }

                return -1;
            }
        }
        return -1;
    }

    /**
     * Get the key of the value for the advanced value map
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since 16 January
     *
     * @param The value linked to the required key
     *
     * @return the key of the value passed in
     */
    public String getKeyFromValueMap(String value) {
        if (includeEmptyValue && value.equals("")) {
            return sEmptyKey;
        }
        Map<String, String> map = valueMap;
        Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            if (value.equals((entry.getValue()))) {
                return "" + entry.getKey();

            }
        }
        return "";
    }

    /**
     * Sets the empty option key for the select box
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since 16 January
     *
     * @param sEmptyKey The value to be used with the empty key
     */
    public void setEmptyKeyReturn(String sEmptyKey) {
        this.sEmptyKey = sEmptyKey;
    }

    /**
     * This function will clear the listbox items
     *
     * @author Michael Barnard <michael.barnard@a24group.com>
     * @since  16 January 2013
     */
    public void emptyListBox() {
        listBox.clear();
        valueMap.clear();
        values = new String[]{""};
    }

    /**
     * Sets focus on the main input of the filter
     *
     * @author Ruan Naude <nauderuan777@gmail.com>
     * @since 24 May 2013
     */
    @Override
    public void setFocusOnMainInput() {
        listBox.setFocus(true);
    }
    
    /**
     * Checks whether the list box is multi select
     * 
     * @author Ryno Hartzer <ryno.hartzer@a24group.com>
     * @since  04 July 2013
     * 
     * @return Whether the list box is multi select
     */
    public boolean isMultiSelect() {
        return listBox.isMultipleSelect();
    }
    
}
