# SafeOps — Design System Specification

## 1. Visual Direction & Identity

The SafeOps design system embodies a **modern, premium, clean enterprise aesthetic** inspired by refined vivo/Funtouch-style layered surfaces, soft translucent containers, vibrant royal blue hero areas, and crisp typography.

Key attributes:
- **Clean & High Contrast**: Deep Navy text on soft surfaces.
- **Translucent & Layered**: Soft surface tints with subtle 1dp borders (`#DCE4F0` light, `#294568` dark).
- **Subtle Elevation**: Low-opacity shadows (`0dp` to `4dp`), strictly avoiding heavy muddy dropshadows.
- **Accessible Touch Targets**: Strict `48dp` minimum touch targets for all interactive buttons and icons.
- **Zero Emojis**: 100% vector iconography via Material Symbols / Icons Extended.

---

## 2. Color Palette

### Light Theme
| Token | Hex | Usage |
|---|---|---|
| `RoyalBluePrimary` | `#1769FF` | Primary brand accent, CTA buttons, active tabs |
| `RoyalBlueDark` | `#0D47C9` | Gradient start, dark blue accents |
| `RoyalBlueLight` | `#EAF2FF` | Active background tints, container highlights |
| `DeepNavy` | `#0B1F4D` | Brand identity, high-emphasis text |
| `BackgroundLight` | `#F4F7FB` | Main application background |
| `SurfaceLight` | `#FFFFFF` | Main cards, input fields, modal containers |
| `SecondarySurfaceLight` | `#F8FAFD` | Input backgrounds, inactive segmented controls |
| `BorderLight` | `#DCE4F0` | Subtle 1dp card and input borders |
| `PrimaryTextLight` | `#102044` | High-contrast body and title typography |
| `SecondaryTextLight` | `#68758E` | Secondary captions, subheadings, labels |
| `DisabledColorLight` | `#9AA6BA` | Inactive controls, placeholder text |
| `SuccessGreen` | `#16A765` | Approved permits, safe status chips |
| `WarningAmber` | `#F59E0B` | Pending reviews, cautious alerts |
| `DangerRed` | `#E94B4B` | Hazard reports, emergency alerts, errors |
| `AccentOrange` | `#FF8A00` | Brand logo "OPS" highlight, hardhat emblem |

### Dark Theme
| Token | Hex | Usage |
|---|---|---|
| `DarkBackground` | `#081A33` | Deep navy background |
| `DarkSurface` | `#102744` | Dark container surface |
| `DarkElevatedSurface` | `#142E50` | Elevated cards, dialog surfaces |
| `DarkBorder` | `#294568` | 1dp border stroke in dark mode |
| `DarkPrimaryText` | `#F5F8FF` | Crisp high-contrast readable text |
| `DarkSecondaryText` | `#AEBBD0` | Secondary descriptions |
| `DarkPrimaryBlue` | `#4D8DFF` | High-visibility primary blue accent |

---

## 3. Gradients

- **SafeOps Hero Gradient**: Vertical gradient `#0D47C9` → `#1769FF` → `#2AA7FF`.
- **Button Gradient**: Horizontal gradient `#1769FF` → `#2E7DFF` → `#3892FF`.
- Gradients are reserved strictly for high-hierarchy hero anchors and primary CTAs.

---

## 4. Typography Scale

| Style | Size / Line Height | Weight | Usage |
|---|---|---|---|
| `display` | 32sp / 40sp | Bold | Hero statistics, splash numbers |
| `headline` | 26sp / 34sp | Bold | Major brand titles |
| `screenTitle` | 22sp / 28sp | SemiBold | Screen top-level headers |
| `sectionTitle` | 18sp / 24sp | SemiBold | Section titles across dashboards |
| `cardTitle` | 16sp / 22sp | SemiBold | Card titles |
| `body` | 14sp / 20sp | Regular | Standard text and inputs |
| `bodyMedium` | 14sp / 20sp | Medium | Form labels and list titles |
| `secondary` | 13sp / 18sp | Regular | Captions and subtitles |
| `caption` | 11sp / 16sp | Normal | Footers, status badges |
| `button` | 15sp / 22sp | SemiBold | Buttons and interactive chips |

---

## 5. Spacing System (8-point System)

- `4dp`, `8dp`, `12dp`, `16dp`, `20dp`, `24dp`, `32dp`, `40dp`
- Default screen padding: `20dp`
- Card internal padding: `16dp`
- Section vertical gap: `24dp`

---

## 6. Corner Radii & Shapes

- `cardStandard`: `16dp`
- `cardFeature`: `20dp`
- `cardSmall`: `12dp`
- `inputField`: `14dp`
- `button`: `14dp`
- `containerTopRounded`: `28dp` (Top start & Top end)
- `pill`: `50%`

---

## 7. Reusable SafeOps Components

1. `SafeOpsButton`: Primary gradient / solid button with loading spinner and 48dp touch target.
2. `SafeOpsOutlinedButton`: Secondary outlined button with 1dp border.
3. `SafeOpsTextField`: Modern rounded input field with leading/trailing icon slots.
4. `SafeOpsPasswordField`: Password input with built-in show/hide eye toggle.
5. `SafeOpsCard`: Rounded container with subtle border and elevation.
6. `SafeOpsGlassCard`: Soft layered translucent card with subtle tint.
7. `SafeOpsGradientHeader`: Hero blue gradient anchor.
8. `SafeOpsBrandHeader` / `SafeOpsShieldEmblem`: Official shield + hardhat vector brand logo.
9. `SafeOpsTopBar`: Material 3 compliant clean top bar.
10. `SafeOpsSectionHeader`: Section header with optional action button.
11. `SafeOpsStatusChip`: Status badge (Success, Warning, Danger, Info, Neutral).
12. `SafeOpsIconButton`: 48dp minimum touch target icon button.
13. `SafeOpsBottomNavigation`: Role-aware bottom navigation bar.
14. `SafeOpsDialog`: Accessible alert/confirmation dialog.
15. `SafeOpsLoading`: Inline and full-screen loading state.
16. `SafeOpsEmptyState`: Vector placeholder for empty states.
17. `SafeOpsErrorState`: Error display with retry CTA.
18. `SafeOpsPrimaryAction`: Fixed/floating bottom CTA bar.
