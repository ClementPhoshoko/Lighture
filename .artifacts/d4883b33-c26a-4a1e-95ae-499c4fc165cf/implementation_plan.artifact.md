# Implementation Plan — Refined Settings List Background & Shadows

Update the visual style of the settings lists in the Profile fragment to use a more premium, branded glassmorphic appearance with enhanced depth and shadows.

## User Review Required

> [!NOTE]
> I will be updating the `bg_glass_card.xml` drawable directly. Since this drawable is exclusively used in the Profile fragment's section cards, this change will cleanly target the settings lists without affecting other "modern" glass cards in the app.

> [!IMPORTANT]
> To ensure the new "soft shadow" layer in the drawable is visible, I will verify that `clipToPadding` and `clipChildren` are disabled on the container layouts in `fragment_profile.xml`.

## Proposed Changes

### Design & Resources

#### [MODIFY] [bg_glass_card.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/drawable/bg_glass_card.xml)
Refactor to a `layer-list` containing:
1. **Soft Shadow Layer**: A shape with a subtle gradient to simulate a large, soft ambient shadow.
2. **Glass Surface**: A shape using `glass_background_red` for a sophisticated branded tint.
3. **Refractive Border**: A dual-layered stroke to give the edge a polished glass look.

---

### Layout Adjustments

#### [MODIFY] [fragment_profile.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/fragment_profile.xml)
Ensure the `LinearLayout` and `NestedScrollView` containers allow shadows to be rendered outside their bounds.

#### [MODIFY] [view_profile_summary.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/view_profile_summary.xml)
#### [MODIFY] [view_profile_preferences.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/view_profile_preferences.xml)
#### [MODIFY] [view_profile_account.xml](file:///C:/Users/User/Desktop/Dev/Lighture/app/src/main/res/layout/view_profile_account.xml)
Ensure the `MaterialCardView` containers have adequate margins to prevent shadow clipping.

## Verification Plan

### Manual Verification
- Deploy to an Android Emulator/Device.
- Navigate to the **Profile** tab.
- Inspect the **Summary**, **Preferences**, and **Account** cards.
- Verify:
    - The new subtle red tint is visible and cohesive.
    - The soft shadow provides a "floating" effect compared to the current flat look.
    - All text and items remain perfectly legible.
