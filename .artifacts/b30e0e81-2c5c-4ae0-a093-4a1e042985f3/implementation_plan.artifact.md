# Redesign Filter Chips to Tab-Style Navigation

Redesign the existing filter chips (used in Recipe and Fridge pages) to follow a modern, minimal tab-navigation style. This involves replacing the pill-shaped chips with text labels and a bottom indicator for the selected state.

## Proposed Changes

### [Component] UI Resources

#### [NEW] [bg_filter_indicator.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/drawable/bg_filter_indicator.xml)
Create a rounded rectangle drawable for the selection indicator using `brand_primary`.

#### [MODIFY] [item_filter_chip.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/item_filter_chip.xml)
- Change root to `LinearLayout` (vertical).
- Add `TextView` for the label.
- Add a `View` for the bottom indicator.
- Set appropriate padding and spacing (generous horizontal spacing).
- Remove the `com.google.android.material.chip.Chip` usage.

#### [MODIFY] [view_filter_chip_bar.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/view_filter_chip_bar.xml)
- Wrap `RecyclerView` in a `LinearLayout`.
- Add a subtle horizontal divider (`@color/divider`) underneath the row.

### [Component] Adapter Logic

#### [MODIFY] [FilterChipAdapter.java](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/java/com/example/lighture/FilterChipAdapter.java)
- Update `FilterViewHolder` to reference the new `TextView` and indicator `View`.
- Update `onBindViewHolder` to:
    - Set the text label.
    - Toggle typography (stronger for selected).
    - Toggle indicator visibility.
    - Remove icon handling as per the "clean text label" requirement.

## Verification Plan

### Manual Verification
- Deploy the app and navigate to the Recipes and Fridge pages.
- Verify that the filter chips now appear as text tabs.
- Check that the selected filter has bold typography and a bottom indicator.
- Ensure the indicator width matches the label width.
- Verify horizontal scrolling works correctly.
- Check that the divider is visible under the entire row.
