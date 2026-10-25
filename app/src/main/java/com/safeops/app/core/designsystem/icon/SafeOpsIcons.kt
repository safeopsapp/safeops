package com.safeops.app.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Centralized icon registry using Material Symbols / Icons.
 * Strict rule: No emojis anywhere in the system.
 */
object SafeOpsIcons {
    // Auth
    val Email: ImageVector = Icons.Outlined.Email
    val Lock: ImageVector = Icons.Outlined.Lock
    val LockOpen: ImageVector = Icons.Outlined.LockOpen
    val Visibility: ImageVector = Icons.Outlined.Visibility
    val VisibilityOff: ImageVector = Icons.Outlined.VisibilityOff
    val Key: ImageVector = Icons.Outlined.Key
    val Google: ImageVector = Icons.Outlined.AccountBalance // placeholder for Google icon

    // Navigation
    val ArrowForward: ImageVector = Icons.AutoMirrored.Outlined.ArrowForward
    val ArrowBack: ImageVector = Icons.AutoMirrored.Outlined.ArrowBack
    val Menu: ImageVector = Icons.Outlined.Menu

    // Roles
    val Worker: ImageVector = Icons.Outlined.Engineering
    val Admin: ImageVector = Icons.Outlined.AdminPanelSettings
    val SafetyOfficer: ImageVector = Icons.Outlined.Security
    val Engineer: ImageVector = Icons.Outlined.Construction

    // App Sections
    val Home: ImageVector = Icons.Outlined.Home
    val Projects: ImageVector = Icons.Outlined.Business
    val Permits: ImageVector = Icons.AutoMirrored.Outlined.Assignment
    val Hazards: ImageVector = Icons.Outlined.WarningAmber
    val Incidents: ImageVector = Icons.Outlined.ReportProblem
    val Inspections: ImageVector = Icons.Outlined.CheckCircle
    val Documents: ImageVector = Icons.Outlined.Description
    val Notifications: ImageVector = Icons.Outlined.Notifications
    val Profile: ImageVector = Icons.Outlined.Person
    val QRScanner: ImageVector = Icons.Outlined.QrCodeScanner
    val QrCode: ImageVector = Icons.Outlined.QrCode
    val Analytics: ImageVector = Icons.Outlined.Analytics
    val Settings: ImageVector = Icons.Outlined.Settings
    val Logout: ImageVector = Icons.AutoMirrored.Outlined.Logout

    // UI Actions
    val Refresh: ImageVector = Icons.Outlined.Refresh
    val Close: ImageVector = Icons.Outlined.Close
    val Search: ImageVector = Icons.Outlined.Search
    val Info: ImageVector = Icons.Outlined.Info
    val Check: ImageVector = Icons.Outlined.CheckCircle
    val Circle: ImageVector = Icons.Outlined.StarOutline
    val Warning: ImageVector = Icons.Outlined.WarningAmber
    val Error: ImageVector = Icons.Outlined.ErrorOutline
    val Help: ImageVector = Icons.AutoMirrored.Outlined.Help
    val Edit: ImageVector = Icons.Outlined.Edit
    val Copy: ImageVector = Icons.Outlined.ContentCopy
    val DropdownArrow: ImageVector = Icons.Outlined.ExpandMore

    // Organization / Registration
    val Person: ImageVector = Icons.Outlined.Person
    val Phone: ImageVector = Icons.Outlined.Phone
    val Location: ImageVector = Icons.Outlined.LocationOn
    val Building: ImageVector = Icons.Outlined.Business
    val Business: ImageVector = Icons.Outlined.Business
    val Badge: ImageVector = Icons.Outlined.Badge
    val Shield: ImageVector = Icons.Outlined.Shield
    val Web: ImageVector = Icons.Outlined.Language

    // Dashboard Aliases
    val Document: ImageVector = Icons.Outlined.Description
    val Clock: ImageVector = Icons.Outlined.Refresh
    val Checklist: ImageVector = Icons.Outlined.CheckCircle
    val CheckCircle: ImageVector = Icons.Outlined.CheckCircle
    val Calendar: ImageVector = Icons.Outlined.Badge
    val AddDocument: ImageVector = Icons.Outlined.Edit
    val Group: ImageVector = Icons.Outlined.Person
    val Siren: ImageVector = Icons.Outlined.ReportProblem
    val Add: ImageVector = Icons.Outlined.Edit
    val Date: ImageVector = Icons.Outlined.Badge
    val Time: ImageVector = Icons.Outlined.Refresh
    val Save: ImageVector = Icons.Outlined.CheckCircle
    val Camera: ImageVector = Icons.Outlined.Visibility
    val Alert: ImageVector = Icons.Outlined.ErrorOutline
}
