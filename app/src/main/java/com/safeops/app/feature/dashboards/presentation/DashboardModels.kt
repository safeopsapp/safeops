package com.safeops.app.feature.dashboards.presentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.safeops.app.core.designsystem.icon.SafeOpsIcons
import com.safeops.app.domain.model.Role

data class UserProfileMock(
    val id: String,
    val name: String,
    val roleTitle: String,
    val initials: String,
    val role: Role,
    val organization: String,
    val project: String,
    val siteLocation: String
)

data class KpiMetric(
    val count: String,
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val containerColor: Color,
    val iconTint: Color
)

data class QuickActionItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconContainerColor: Color,
    val iconTint: Color,
    val targetRoute: String
)

data class PermitMock(
    val id: String,
    val permitNumber: String,
    val title: String,
    val typeName: String,
    val location: String,
    val dateRange: String,
    val timeRange: String,
    val statusText: String,
    val statusColor: Color,
    val timeAgo: String
)

data class ScheduleItemMock(
    val time: String,
    val title: String,
    val location: String,
    val statusTag: String,
    val tagColor: Color
)

data class ActivityItemMock(
    val title: String,
    val subtitle: String,
    val timeAgo: String,
    val icon: ImageVector,
    val iconColor: Color
)

data class PendingActionMock(
    val title: String,
    val permitRef: String,
    val actionText: String,
    val actionColor: Color
)

data class PermitTypeCountMock(
    val name: String,
    val count: Int,
    val totalRatio: Float,
    val barColor: Color
)

object DashboardMockData {
    val adminProfile = UserProfileMock(
        id = "ADM-001",
        name = "Alex Morgan",
        roleTitle = "Admin",
        initials = "AM",
        role = Role.ADMIN,
        organization = "Apex Infrastructure Ltd",
        project = "Chennai Metro Site",
        siteLocation = "Zone B • Main Corridor"
    )

    val engineerProfile = UserProfileMock(
        id = "ENG-104",
        name = "Marcus Vance",
        roleTitle = "Engineer",
        initials = "MV",
        role = Role.ENGINEER,
        organization = "Apex Infrastructure Ltd",
        project = "Chennai Metro Project",
        siteLocation = "Zone B • Building A - Level 3"
    )

    val ehsProfile = UserProfileMock(
        id = "EHS-209",
        name = "S. Shankar",
        roleTitle = "Safety Officer",
        initials = "SS",
        role = Role.SAFETY_OFFICER,
        organization = "Apex Infrastructure Ltd",
        project = "Chennai Metro Project",
        siteLocation = "Building A • Level 3"
    )

    val workerProfile = UserProfileMock(
        id = "WRK-512",
        name = "Ramesh Kumar",
        roleTitle = "Field Specialist",
        initials = "RK",
        role = Role.WORKER,
        organization = "Apex Infrastructure Ltd",
        project = "Chennai Metro Project",
        siteLocation = "Building A • Level 3"
    )

    // Admin KPIs
    val adminKpis = listOf(
        KpiMetric("24", "Total Permits", "All categories", SafeOpsIcons.Document, Color(0xFFEBF2FF), Color(0xFF0878FF)),
        KpiMetric("6", "Pending Review", "Needs action", SafeOpsIcons.Clock, Color(0xFFFFF4E5), Color(0xFFE67E22)),
        KpiMetric("12", "Active Permits", "Across sites", SafeOpsIcons.CheckCircle, Color(0xFFE6F7ED), Color(0xFF27AE60)),
        KpiMetric("3", "Expired Permits", "Requires audit", SafeOpsIcons.Warning, Color(0xFFFDE8E8), Color(0xFFE74C3C))
    )

    // Engineer KPIs
    val engineerKpis = listOf(
        KpiMetric("8", "My Permits", "2 Under Review", SafeOpsIcons.Document, Color(0xFFEBF2FF), Color(0xFF0878FF)),
        KpiMetric("3", "Pending Action", "Need Attention", SafeOpsIcons.Clock, Color(0xFFFFF4E5), Color(0xFFE67E22)),
        KpiMetric("5", "Approved", "This Month", SafeOpsIcons.CheckCircle, Color(0xFFE6F7ED), Color(0xFF27AE60)),
        KpiMetric("1", "Inspection Due", "Scheduled Today", SafeOpsIcons.Calendar, Color(0xFFF3E8FF), Color(0xFF9B59B6))
    )

    // EHS KPIs
    val ehsKpis = listOf(
        KpiMetric("14", "Pending Review", "Permits waiting", SafeOpsIcons.Document, Color(0xFFEBF2FF), Color(0xFF0878FF)),
        KpiMetric("26", "Active Permits", "Across all sites", SafeOpsIcons.CheckCircle, Color(0xFFE6F7ED), Color(0xFF27AE60)),
        KpiMetric("8", "Site Inspections", "This week", SafeOpsIcons.Checklist, Color(0xFFFFF4E5), Color(0xFFE67E22)),
        KpiMetric("5", "Open Hazards", "Need action", SafeOpsIcons.Warning, Color(0xFFFDE8E8), Color(0xFFE74C3C))
    )

    // Worker KPIs
    val workerKpis = listOf(
        KpiMetric("4", "Today's Work", "Assigned tasks", SafeOpsIcons.Checklist, Color(0xFFEBF2FF), Color(0xFF0878FF)),
        KpiMetric("2", "Active Permits", "Site access", SafeOpsIcons.Document, Color(0xFFE6F7ED), Color(0xFF27AE60)),
        KpiMetric("1", "Briefing Done", "Morning toolbox", SafeOpsIcons.CheckCircle, Color(0xFFFFF4E5), Color(0xFFE67E22)),
        KpiMetric("0", "Open Hazards", "Clean site", SafeOpsIcons.Shield, Color(0xFFFDE8E8), Color(0xFFE74C3C))
    )

    // Quick Actions
    val adminQuickActions = listOf(
        QuickActionItem("a1", "Create Permit", "Request a new permit", SafeOpsIcons.AddDocument, Color(0xFFEBF2FF), Color(0xFF0878FF), "create_permit"),
        QuickActionItem("a2", "Manage Workers", "Site access & roles", SafeOpsIcons.Group, Color(0xFFF3E8FF), Color(0xFF9B59B6), "manage_workers"),
        QuickActionItem("a3", "Add Project", "New construction site", SafeOpsIcons.Business, Color(0xFFFFF4E5), Color(0xFFE67E22), "add_project"),
        QuickActionItem("a4", "Invite Code", "Generate worker code", SafeOpsIcons.QrCode, Color(0xFFE6F7ED), Color(0xFF27AE60), "generate_invite")
    )

    val engineerQuickActions = listOf(
        QuickActionItem("e1", "Create Permit", "Request a new permit", SafeOpsIcons.AddDocument, Color(0xFFEBF2FF), Color(0xFF0878FF), "create_permit"),
        QuickActionItem("e2", "Report Hazard", "Unsafe condition", SafeOpsIcons.Warning, Color(0xFFFFF4E5), Color(0xFFE67E22), "report_hazard"),
        QuickActionItem("e3", "Report Incident", "Accident or near miss", SafeOpsIcons.Siren, Color(0xFFFDE8E8), Color(0xFFE74C3C), "report_incident"),
        QuickActionItem("e4", "Start Inspection", "Site inspection", SafeOpsIcons.Checklist, Color(0xFFE6F7ED), Color(0xFF27AE60), "start_inspection")
    )

    val ehsQuickActions = listOf(
        QuickActionItem("s1", "Review Permits", "Pending approvals", SafeOpsIcons.Document, Color(0xFFEBF2FF), Color(0xFF0878FF), "review_permits"),
        QuickActionItem("s2", "Conduct Inspection", "Site checklist", SafeOpsIcons.Checklist, Color(0xFFE6F7ED), Color(0xFF27AE60), "conduct_inspection"),
        QuickActionItem("s3", "View Hazards", "Open & assigned", SafeOpsIcons.Warning, Color(0xFFFFF4E5), Color(0xFFE67E22), "view_hazards"),
        QuickActionItem("s4", "View Incidents", "Recent reports", SafeOpsIcons.Siren, Color(0xFFFDE8E8), Color(0xFFE74C3C), "view_incidents")
    )

    val workerQuickActions = listOf(
        QuickActionItem("w1", "My Work", "Daily assignments", SafeOpsIcons.Checklist, Color(0xFFEBF2FF), Color(0xFF0878FF), "my_work"),
        QuickActionItem("w2", "Report Hazard", "Report unsafe area", SafeOpsIcons.Warning, Color(0xFFFFF4E5), Color(0xFFE67E22), "report_hazard"),
        QuickActionItem("w3", "Report Incident", "Accident report", SafeOpsIcons.Siren, Color(0xFFFDE8E8), Color(0xFFE74C3C), "report_incident"),
        QuickActionItem("w4", "Safety Briefing", "Toolbox meeting log", SafeOpsIcons.Shield, Color(0xFFE6F7ED), Color(0xFF27AE60), "safety_briefing")
    )

    // Permits Mock List
    val engineerPermits = listOf(
        PermitMock("P-1026", "PR-1026", "Hot Work Permit", "Hot Work", "Building A • Level 3", "26 Sep 2026", "09:00 AM – 05:00 PM", "Under Review", Color(0xFFE67E22), "10 min ago"),
        PermitMock("P-1025", "PR-1025", "Work at Height Permit", "Work at Height", "Tower 2 • Exterior Structure", "25 Sep 2026", "08:00 AM – 06:00 PM", "Approved", Color(0xFF27AE60), "2 hrs ago"),
        PermitMock("P-1024", "PR-1024", "Excavation Work Permit", "Excavation", "Building C • North Zone", "24 Sep 2026", "08:00 AM – 04:00 PM", "Active", Color(0xFF0878FF), "1 day ago")
    )

    val ehsReviewQueue = listOf(
        PermitMock("P-1028", "PR-1028", "Hot Work Permit", "Hot Work", "Building A • Level 3", "Today", "09:00 AM – 05:00 PM", "Under Review", Color(0xFFE67E22), "30 min ago"),
        PermitMock("P-1027", "PR-1027", "Work at Height Permit", "Work at Height", "Tower 2 • Exterior", "Today", "08:00 AM – 06:00 PM", "Under Review", Color(0xFFE67E22), "1 hr ago"),
        PermitMock("P-1026", "PR-1026", "Excavation Work Permit", "Excavation", "North Zone", "Today", "07:00 AM – 03:00 PM", "Under Review", Color(0xFFE67E22), "2 hrs ago"),
        PermitMock("P-1025", "PR-1025", "General Work Permit", "General Work", "Building B", "Today", "08:00 AM – 05:00 PM", "Under Review", Color(0xFFE67E22), "3 hrs ago")
    )

    // Schedules
    val engineerSchedule = listOf(
        ScheduleItemMock("09:00 AM", "Safety Meeting", "Site Office", "Ongoing", Color(0xFF0878FF)),
        ScheduleItemMock("10:00 AM", "Site Walkthrough", "Building A – Level 3", "Upcoming", Color(0xFF7F8C8D)),
        ScheduleItemMock("11:30 AM", "Permit Submission", "Hot Work – PR-1026", "Pending", Color(0xFFE67E22))
    )

    val ehsInspections = listOf(
        ScheduleItemMock("09:00 AM", "Scaffold Inspection", "Building A – Level 3", "Today", Color(0xFF0878FF)),
        ScheduleItemMock("11:00 AM", "Hot Work Area Check", "Building B – Mechanical Zone", "Today", Color(0xFFE67E22)),
        ScheduleItemMock("02:00 PM", "Lifting Equipment", "Site Yard", "Today", Color(0xFF9B59B6)),
        ScheduleItemMock("04:00 PM", "Confined Space Area", "Utility Block", "Today", Color(0xFF7F8C8D))
    )

    // Recent Activities
    val recentActivities = listOf(
        ActivityItemMock("Permit PR-1026 submitted", "Hot Work Permit • Building A Level 3", "10 min ago", SafeOpsIcons.Document, Color(0xFFE67E22)),
        ActivityItemMock("Permit PR-1024 approved", "Excavation Permit • North Zone", "2 hrs ago", SafeOpsIcons.CheckCircle, Color(0xFF27AE60)),
        ActivityItemMock("New worker assigned", "Ramesh Kumar – PR-1024", "4 hrs ago", SafeOpsIcons.Group, Color(0xFF9B59B6)),
        ActivityItemMock("Inspection completed", "Scaffold Check – Building A Level 2", "6 hrs ago", SafeOpsIcons.Checklist, Color(0xFF0878FF))
    )

    // Pending Actions for Admin
    val adminPendingActions = listOf(
        PendingActionMock("Review Permit PR-1025", "Excavation Work", "Review", Color(0xFFE67E22)),
        PendingActionMock("Verify Documents PR-1026", "Hot Work", "Documents", Color(0xFF0878FF)),
        PendingActionMock("Assign Safety Officer", "Confined Space", "Assign", Color(0xFF9B59B6)),
        PendingActionMock("Permit Expiring Soon", "Work at Height", "1 day", Color(0xFFE74C3C))
    )

    // Admin Permit Types
    val permitTypesCount = listOf(
        PermitTypeCountMock("General Work", 8, 0.8f, Color(0xFF0878FF)),
        PermitTypeCountMock("Hot Work", 5, 0.5f, Color(0xFFE67E22)),
        PermitTypeCountMock("Work at Height", 4, 0.4f, Color(0xFF9B59B6)),
        PermitTypeCountMock("Excavation", 3, 0.3f, Color(0xFFF1C40F)),
        PermitTypeCountMock("Confined Space", 2, 0.2f, Color(0xFFE74C3C))
    )
}
