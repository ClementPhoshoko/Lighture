# Implementation Plan - Reusable Global Header

Move the page headers from individual fragments into `MainActivity` to create a consistent, reusable header across the entire application, similar to the `BottomNavigationView`.

## Proposed Changes

### [Component Name] UI Shell

#### [MODIFY] [activity_main.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/activity_main.xml)
- Add `view_header_glass` to the top of the layout.
- Adjust `NavHostFragment` constraints to sit below the header.

### State Management

#### [NEW] [HeaderViewModel.java](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/java/com/example/lighture/HeaderViewModel.java)
- Create a `ViewModel` to hold header state: `title`, `subtitle`, `actionIconRes`, `actionText`, and `onActionClick`.

### Logic Integration

#### [MODIFY] [MainActivity.java](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/java/com/example/lighture/MainActivity.java)
- Initialize `HeaderViewModel`.
- Observe `HeaderViewModel` and update the global header UI when state changes.

#### [MODIFY] [BaseFragment.java](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/java/com/example/lighture/BaseFragment.java) (Optional but recommended)
- Create a base fragment to handle `HeaderViewModel` access.

#### [MODIFY] Individual Fragments
- [HomeFragment.java](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/java/com/example/lighture/HomeFragment.java)
- [RecipesFragment.java](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/java/com/example/lighture/RecipesFragment.java)
- [FridgeFragment.java](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/java/com/example/lighture/FridgeFragment.java)
- [ProfileFragment.java](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/java/com/example/lighture/ProfileFragment.java)
- Update fragments to set their header configuration in `HeaderViewModel`.

#### [DELETE] / [MODIFY] Fragment Layouts
- Remove included headers from `fragment_home.xml`, `fragment_recipes.xml`, etc.

## Verification Plan

### Automated Tests
- N/A (UI focused change, manual verification preferred for layout consistency)

### Manual Verification
- Deploy the app and navigate through all tabs.
- Verify that the header updates its title and subtitle correctly for each page.
- Verify that the action buttons (like Notifications on Home, Scan on Fridge) still function correctly through the global header.
- Ensure the glass effect and ambient glow remain consistent.
