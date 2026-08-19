# Implementation Plan — Glassmorphic Dashboard Cards

Build a row of three premium glassmorphic statistic cards for the Home overview, matching the reference's visual language and integrating with the existing design system.

## User Review Required

> [!IMPORTANT]
> The implementation uses Android XML resources. Since standard Android Views do not support real-time backdrop blur (like CSS `backdrop-filter`), I will approximate the glass effect using layered translucency, subtle gradients, and elevation-based shadows.

> [!NOTE]
> I will introduce a `res/layout-w600dp` version of the overview to handle the desktop/tablet horizontal layout, while the default `res/layout` will stack cards vertically for mobile as requested.

## Proposed Changes

### Design Tokens & Resources

#### [NEW] [glass_tokens.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/values/glass_tokens.xml)
Define glass-specific semantic tokens (translucency levels, tint opacities) while referencing existing brand colors.

#### [NEW] [bg_glass_card_stat.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/drawable/bg_glass_card_stat.xml)
Base drawable for the glass cards using `layer-list` to combine a soft shadow, a tinted translucent surface, and a thin "refractive" border.

#### [NEW] [bg_glass_icon_container.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/drawable/bg_glass_icon_container.xml)
Circular glass container for the card icons.

#### [NEW] [bg_glass_pill.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/drawable/bg_glass_pill.xml)
Pill-shaped translucent background for the bottom status elements.

#### [NEW] [ic_decorative_blob.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/drawable/ic_decorative_blob.xml)
Vector drawable representing the abstract blurred decorative shapes.

---

### Layout Changes

#### [MODIFY] [view_home_overview.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/view_home_overview.xml)
Update the layout to use a vertical stack (for mobile) and implement the new glassmorphic card structure:
- `ConstraintLayout` for each card's internal arrangement.
- Positioning: Icon (top-left), Menu (top-right), Stat (center), Label (below stat), Pill (bottom).
- Adding decorative blob images.

#### [NEW] [view_home_overview.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout-w600dp/view_home_overview.xml)
Create a horizontal row layout for tablets and desktops, preserving the same visual style but optimizing for wider screens.

---

### UI Components

#### [NEW] [layout_glass_stat_card.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/layout_glass_stat_card.xml)
Extracted reusable layout for a single stat card to avoid duplication and ensure consistency across the three metrics.

## Verification Plan

### Automated Tests
- N/A (UI layout task).

### Manual Verification
- Deploy to an Android Emulator/Device.
- Verify visual appearance: transparency, borders, shadows, and color tints.
- Check responsiveness:
    - Mobile: Cards should stack vertically.
    - Tablet/Desktop (e.g., Pixel C or Resizable emulator): Cards should align horizontally.
- Verify color mapping:
    - Items tracked -> Green/Success tint.
    - Expiring soon -> Orange/Warning tint.
    - Waste saved -> Red/Danger tint.
