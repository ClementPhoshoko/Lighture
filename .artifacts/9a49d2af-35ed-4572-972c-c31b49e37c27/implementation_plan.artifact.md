# Responsive Layout Fixes for Home and Profile Fragments

This plan addresses responsiveness issues where UI components in `fragment_home.xml` and other pages "fail to sit properly" or are "pushed" when system font sizes are large or on different device widths (e.g., Samsung A12 vs A24).

## User Review Required

> [!IMPORTANT]
> The fixes involve changing some horizontal layouts (like the 3-column stats on Home or 4-column stats on Profile) to be more flexible. This might result in items wrapping to a new line on very narrow screens or with very large fonts, which is necessary to prevent text clipping or layout breakage.

## Proposed Changes

### [Home & Overview Components]

#### [MODIFY] [view_home_hero.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/view_home_hero.xml)
- Replace fixed `android:layout_height="@dimen/hero_button_height"` with `android:minHeight="@dimen/hero_button_height"` and `android:layout_height="wrap_content"` for buttons.
- Use `androidx.constraintlayout.helper.widget.Flow` to manage the Hero buttons (`heroTakePhoto`, `heroLiveScan`) so they can wrap to a new line if the text is too large for a single row.
- Ensure `heroSubtitle` uses `0dp` width with constraints to `heroImage` to allow proper wrapping without pushing the image.

#### [MODIFY] [view_home_overview.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/view_home_overview.xml)
- Replace the horizontal `LinearLayout` with a `ConstraintLayout` using `Flow` or adjust weights/padding to allow for text expansion.
- Alternatively, wrap the stat cards in a `HorizontalScrollView` if they must stay in one row, but a wrapping grid is preferred for accessibility.

#### [MODIFY] [layout_glass_stat_card.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/layout_glass_stat_card.xml)
- Change inner `ConstraintLayout` to `wrap_content` height instead of `match_parent` to allow cards to grow vertically if text expands.
- Ensure the `statusPill` doesn't overlap content by using proper vertical constraints.

### [Profile Components]

#### [MODIFY] [view_profile_summary.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/view_profile_summary.xml)
- Fix the 4-column statistics row. Replace the weighted `LinearLayout` with a `Flow` or `GridLayout` so that items can wrap to a second row (2x2) on narrow screens or with large fonts.
- Remove `android:maxLines="1"` and `android:ellipsize="end"` from stat labels where possible, or ensure the container can handle multiple lines.

### [Global Dimensions]

#### [MODIFY] [dimens.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/values/dimens.xml)
- Audit and adjust spacing tokens if they are too aggressive for smaller devices.

## Verification Plan

### Automated Tests
- Build and run the app to ensure no compilation errors after XML changes.

### Manual Verification
- Deploy to a device/emulator.
- **Font Scaling Test**: Go to System Settings -> Display -> Font size and style, and set the font to the maximum. Verify that the Home hero buttons, Stat cards, and Profile summary remain readable and correctly laid out (wrapping instead of overlapping).
- **Screen Size Test**: Test on devices with different aspect ratios/widths (A12 vs A24) to ensure the `Flow` layouts adapt correctly.
