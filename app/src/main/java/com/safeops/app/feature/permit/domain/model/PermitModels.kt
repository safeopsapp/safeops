package com.safeops.app.feature.permit.domain.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

/**
 * Official SafeOps / Springfield Permit Types.
 */
enum class PermitType(
    val code: String,
    val title: String,
    val description: String
) {
    GENERAL_WORK(
        code = "SPR/HSE/WP/01",
        title = "General Work Permit",
        description = "Standard permit for general site construction, maintenance, and routine civil works."
    ),
    EXCAVATION(
        code = "SPR/HSE/WP/02",
        title = "Excavation Work Permit",
        description = "Required for trenching, foundation digging, utility underground works, and earthworks."
    ),
    HOT_WORK(
        code = "SPR/HSE/WP/03",
        title = "Hot Work Permit",
        description = "Required for welding, cutting, grinding, torch burning, and flame-producing operations."
    ),
    LIFTING(
        code = "SPR/HSE/WP/04",
        title = "Lifting Work Permit",
        description = "Required for heavy crane operations, rigged loads, tandem lifting, and mechanical hoists."
    ),
    WORK_AT_HEIGHT(
        code = "SPR/HSE/WP/05",
        title = "Work at Height Permit",
        description = "Required for works on scaffolding above 2 meters height, roof access, and elevated platforms."
    ),
    PILING(
        code = "SPR/HSE/WP/09",
        title = "Piling Work Permit",
        description = "Required for driven, bored, or cast-in-situ piling rig operations and foundation piling."
    ),
    NIGHT_WORK(
        code = "SPR/HSE/WP/08",
        title = "Night Work Permit",
        description = "Required for works conducted outside normal daylight hours (6:00 PM to 6:00 AM)."
    ),
    DEMOLITION(
        code = "SPR/HSE/WP/11",
        title = "Demolition Work Permit",
        description = "Required for structural tearing, wall removal, architectural fitting removal, and wrecking."
    )
}

/**
 * Permit Workflow Status.
 */
enum class PermitStatus(val displayName: String, val colorHex: Long) {
    DRAFT("Draft", 0xFF64748B),
    SUBMITTED("Submitted", 0xFF0878FF),
    UNDER_EHS_REVIEW("Under EHS Review", 0xFFF59E0B),
    CORRECTION_REQUIRED("Correction Required", 0xFFEF4444),
    RESUBMITTED("Resubmitted", 0xFF3B82F6),
    PENDING_APPROVAL("Pending Approval", 0xFF8B5CF6),
    APPROVED("Approved", 0xFF10B981),
    REJECTED("Rejected", 0xFFDC2626),
    ACTIVE("Active / In Progress", 0xFF059669),
    SUSPENDED("Suspended", 0xFFD97706),
    COMPLETION_REQUESTED("Completion Requested", 0xFF2563EB),
    CLOSURE_REVIEW("Closure Review", 0xFF4F46E5),
    CLOSED("Closed / Completed", 0xFF475569),
    CANCELLED("Cancelled", 0xFF94A3B8)
}

/**
 * Checklist Response Status.
 */
enum class ChecklistStatus(val label: String, val code: String) {
    OK("OK", "OK"),
    NOT_OK("NOT OK", "X"),
    NOT_APPLICABLE("N/A", "NA")
}

/**
 * Sub-Permit Attachment Options (Official General & Night Work attachment list).
 */
enum class SubPermitAttachment(val label: String) {
    HOT_WORKS("Hot Works"),
    WORK_AT_HEIGHT("Work at Height"),
    LIFTING_WORK("Lifting Work"),
    EXCAVATION_WORK("Excavation Work"),
    NIGHT_WORK("Night Work"),
    CONFINED_SPACE("Confined Space"),
    WORK_AT_HEIGHT_ABOVE_2M("Work at Height above 2 mtr"),
    PILING_WORK("Piling Work"),
    WORK_AT_HEIGHT_OTHER("Work at Height other than scaffold"),
    DEMOLITION_WORK("Demolition Work")
}

/**
 * Structured Signature Model.
 */
data class SignatureData(
    val signerId: String = "",
    val signerName: String = "",
    val role: String = "",
    val signaturePath: List<Pair<Float, Float>> = emptyList(),
    val signedAtTimestamp: Long = System.currentTimeMillis()
) {
    val isSigned: Boolean get() = signerName.isNotBlank() && signaturePath.isNotEmpty()
}

/**
 * Checklist Item Response.
 */
data class ChecklistItemResponse(
    val itemIndex: Int,
    val itemText: String,
    val status: ChecklistStatus? = null,
    val observation: String = "",
    val correctiveAction: String = "",
    val inspectorSignature: SignatureData? = null
) {
    val isValid: Boolean
        get() = when (status) {
            null -> false
            ChecklistStatus.NOT_OK -> observation.isNotBlank() && correctiveAction.isNotBlank()
            ChecklistStatus.OK, ChecklistStatus.NOT_APPLICABLE -> true
        }
}

/**
 * Daily Checklist Review (Days 1 to 7).
 */
data class DailyChecklistReview(
    val dayNumber: Int,
    val responses: Map<Int, ChecklistItemResponse> = emptyMap(),
    val inspectorSignature: SignatureData? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Permit Common Header.
 */
data class PermitHeader(
    val permitNumber: String = "",
    val workPerformedBy: String = "",
    val location: String = "",
    val project: String = "Sairam Metro Line - Phase 2"
)

/**
 * Schedule & Worker Count.
 */
data class PermitSchedule(
    val workDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val workerCount: Int = 1
)

/**
 * Specialized Personnel Blocks.
 */
data class PermitPersonnel(
    val siteEngineerName: String = "",
    val siteEngineerContact: String = "",
    val liftingSupervisorName: String = "",
    val liftingSupervisorContact: String = "",
    val riggerSignalManName: String = "",
    val riggerSignalManContact: String = "",
    val craneOperatorName: String = "",
    val craneOperatorContact: String = "",
    val craneOperatorLicenseNo: String = "",
    val pilingSupervisorName: String = "",
    val pilingSupervisorContact: String = "",
    val rigOperatorName: String = "",
    val rigOperatorContact: String = "",
    val rigOperatorLicenseNo: String = ""
)

/**
 * Contractor HSE Assessment (Part 1B).
 */
data class PermitHseAssessment(
    val statement1Briefed: Boolean = false,
    val statement2AreaSafe: Boolean = false,
    val statement3MeasuresTaken: Boolean = false,
    val contractorEngineerSig: SignatureData? = null,
    val contractorHseSig: SignatureData? = null,
    val contractorPmSig: SignatureData? = null
) {
    val isAllAcknowledged: Boolean get() = statement1Briefed && statement2AreaSafe && statement3MeasuresTaken
}

/**
 * Final Review (Part 2).
 */
data class PermitReview(
    val engineerStatement: Boolean = false,
    val hseStatement: Boolean = false,
    val sprEngineerSig: SignatureData? = null,
    val sprHseSig: SignatureData? = null,
    val reviewDate: String = "",
    val reviewTime: String = ""
)

/**
 * Approval (Part 3).
 */
data class PermitApproval(
    val hiracBriefed: Boolean = false,
    val controlsInPlace: Boolean = false,
    val riskReduced: Boolean = false,
    val approverName: String = "",
    val approverRole: String = "SPR Project Manager / HOD",
    val approverSig: SignatureData? = null,
    val approvalDate: String = "",
    val approvalTime: String = ""
)

/**
 * Daily Endorsement (Part 4 - Days 1 to 7).
 */
data class DailyEndorsement(
    val dayNumber: Int,
    val sprEngineerSig: SignatureData? = null,
    val sprHseSig: SignatureData? = null,
    val date: String = ""
)

/**
 * Completion Notification (Part 5).
 */
data class PermitCompletion(
    val workCompleted: Boolean = false,
    val housekeepingCarriedOut: Boolean = false,
    val areaSafe: Boolean = false,
    val contractorName: String = "",
    val contractorSig: SignatureData? = null,
    val completionDate: String = "",
    val completionTime: String = ""
)

/**
 * Final Confirmation (Part 6).
 */
data class PermitConfirmation(
    val workCompleted: Boolean = false,
    val housekeepingCarriedOut: Boolean = false,
    val areaSafe: Boolean = false,
    val sprEngineerName: String = "",
    val sprEngineerSig: SignatureData? = null,
    val confirmationDate: String = "",
    val confirmationTime: String = ""
)

/**
 * Second Page Daily EHS Observation Log (Days 1 to 7).
 */
data class DailyEhsObservation(
    val dayNumber: Int,
    val date: String = "",
    val jobDescriptionWorkmenDetails: String = "",
    val ehsObservation: String = "",
    val siteEngineerSig: SignatureData? = null,
    val ehsInchargeSig: SignatureData? = null
)

/**
 * Comprehensive Permit Domain Model.
 */
data class PermitFormModel(
    val id: String = UUID.randomUUID().toString(),
    val permitType: PermitType = PermitType.GENERAL_WORK,
    val status: PermitStatus = PermitStatus.DRAFT,
    val header: PermitHeader = PermitHeader(),
    val schedule: PermitSchedule = PermitSchedule(),
    val personnel: PermitPersonnel = PermitPersonnel(),
    val descriptionOfWork: String = "",
    val requiredPermitReferences: List<String> = emptyList(), // For Night Work
    val selectedSubPermits: Set<SubPermitAttachment> = emptySet(), // For General & Night Work
    val dailyChecklists: Map<Int, DailyChecklistReview> = emptyMap(),
    val hseAssessment: PermitHseAssessment = PermitHseAssessment(),
    val review: PermitReview = PermitReview(),
    val approval: PermitApproval = PermitApproval(),
    val dailyEndorsements: Map<Int, DailyEndorsement> = emptyMap(),
    val completion: PermitCompletion = PermitCompletion(),
    val confirmation: PermitConfirmation = PermitConfirmation(),
    val dailyObservations: List<DailyEhsObservation> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdByUserId: String = "",
    val createdByName: String = ""
)
