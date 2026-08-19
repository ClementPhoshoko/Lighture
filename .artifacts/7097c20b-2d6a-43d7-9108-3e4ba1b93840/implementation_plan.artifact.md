# Refinement: Home Stat Card Design

This plan polishes the Home Overview stat cards to improve alignment, hierarchy, and visual balance.

## User Review Required

> [!IMPORTANT]
> The card icon will now be centered horizontally, and the menu dots will move to the absolute top-right corner (unaffected by internal padding). The status pill icon will be removed for a cleaner look.

## Proposed Changes

### [Home Components]

#### [MODIFY] [layout_glass_stat_card.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/layout_glass_stat_card.xml)
- **Inner Layout**: Remove `android:padding="@dimen/space_5"` from the inner `ConstraintLayout` to allow the menu to reach the corners.
- **Menu Indicator**: Keep at top-right, but add `marginTop="@dimen/space_2"` and `marginEnd="@dimen/space_2"` for precise positioning.
- **Card Icon (`iconContainer`)**:
    - Center horizontally using `app:layout_constraintEnd_toEndOf="parent"`.
    - Add `android:layout_marginTop="@dimen/space_5"` to maintain vertical balance.
- **Center Content**:
    - Add `android:layout_marginTop="@dimen/space_3"` to separate it from the icon.
    - Add `android:paddingHorizontal="@dimen/space_4"` to ensure text doesn't touch edges.
- **Status Pill**:
    - Remove the `pillIcon` ImageView.
    - Remove `layout_marginStart` from `pillText`.
    - Add `android:layout_marginTop="@dimen/space_3"` to provide vertical breathing room.
    - Add `android:layout_marginBottom="@dimen/space_4"` to anchor it at the bottom with consistent padding.

## Verification Plan

### Manual Verification
- **Visual Check**:
    1. Verify the main card icon is perfectly centered horizontally.
    2. Verify the menu dots are in the top-right corner, further out than before.
    3. Verify the status pill at the bottom has no icon and has a clear gap from the text above it.
    4. Verify the card height adjusts properly if the font is set to "Huge."
