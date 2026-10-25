package com.safeops.app.feature.permit.domain.template

import com.safeops.app.feature.permit.domain.model.PermitType

/**
 * Definition of a specific permit template.
 */
data class PermitTemplate(
    val type: PermitType,
    val checklistItems: List<String>,
    val requiresLiftingTeam: Boolean = false,
    val requiresPilingTeam: Boolean = false,
    val requiresNightWorkReferences: Boolean = false,
    val hasSubPermitAttachments: Boolean = false
)

/**
 * Registry containing official Springfield/SafeOps permit templates.
 */
object PermitTemplateRegistry {

    private val templates: Map<PermitType, PermitTemplate> = mapOf(
        PermitType.GENERAL_WORK to PermitTemplate(
            type = PermitType.GENERAL_WORK,
            checklistItems = listOf(
                "Provision of adequate PPE",
                "Provision of signage and barricade",
                "Sufficient Lighting",
                "Environment aspects eliminated",
                "Adequate ventilation"
            ),
            hasSubPermitAttachments = true
        ),
        PermitType.EXCAVATION to PermitTemplate(
            type = PermitType.EXCAVATION,
            checklistItems = listOf(
                "Excavator/Back Hoe Approved to be used",
                "Barricade, Warning signage, Hazard light to be provided",
                "Sufficient Lighting for night work",
                "Provision of adequate ladders to access",
                "Provision of adequate shoring/ bracing materials",
                "Location of underground services predetermined & Protected/supported",
                "PE design for excavation depth >4m."
            )
        ),
        PermitType.HOT_WORK to PermitTemplate(
            type = PermitType.HOT_WORK,
            checklistItems = listOf(
                "Barricade & Warning signage provision",
                "Standby fire watchman.",
                "Ensure no incompatible work",
                "Fire/sparks containment & Protection",
                "Standby sufficient fire extinguisher",
                "Area free from combustible materials",
                "Flashback Arrestors"
            )
        ),
        PermitType.LIFTING to PermitTemplate(
            type = PermitType.LIFTING,
            checklistItems = listOf(
                "Valid TC, LG & Operator Certificate.",
                "Barricade & Warning Signage Provision",
                "Safe Crane Access & Ground Condition",
                "Outriggers fully extended on steel plates",
                "Colour code of lifting equipment",
                "Aircraft Warning Device.",
                "Proper Electrical Connections & Earthing",
                "Display of Safe Working Load on all Lifting appliances"
            ),
            requiresLiftingTeam = true
        ),
        PermitType.WORK_AT_HEIGHT to PermitTemplate(
            type = PermitType.WORK_AT_HEIGHT,
            checklistItems = listOf(
                "Work personal trained & Vertigo test completed.",
                "Scaffolds certified safe use & no visible damage on scaffold components.",
                "Provision of safety harness and suitable anchorage.",
                "Proper & adequate work platform with toe board.",
                "Adequate & secured Guard rails",
                "Locking device on caster wheels and outriggers provision",
                "Barricade & warning signage provision",
                "Use of tool box and securing of tools"
            )
        ),
        PermitType.PILING to PermitTemplate(
            type = PermitType.PILING,
            checklistItems = listOf(
                "Adjoining structure certified safe",
                "Valid LM, LG & Rig Operator Certificate",
                "Piling Rig in good condition",
                "Certified safe footing and steel plate Rig access",
                "Air craft warning device",
                "Colour code of lifting equipment",
                "Signboards and barricade provision"
            ),
            requiresPilingTeam = true
        ),
        PermitType.NIGHT_WORK to PermitTemplate(
            type = PermitType.NIGHT_WORK,
            checklistItems = listOf(
                "Provision of adequate PPE",
                "Provision of signage and barricade",
                "Sufficient Lighting",
                "Environment aspects eliminated",
                "Adequate ventilation",
                "Availability of First Aider",
                "Provision of Emergency Vehicle"
            ),
            requiresNightWorkReferences = true,
            hasSubPermitAttachments = true
        ),
        PermitType.DEMOLITION to PermitTemplate(
            type = PermitType.DEMOLITION,
            checklistItems = listOf(
                "Adjoining structure protected and made safe",
                "Glass, Cladding and external architectural fittings removed",
                "Gas, electric, water & utilities lines / energy sources isolated / relocated & protected, LOTO provided where applicable",
                "Provision of warning signage's and barricades",
                "Provision of safe access",
                "Safe Removal of debris, loading & unloading",
                "Dust/Oil leaks control in place."
            )
        )
    )

    fun getTemplate(type: PermitType): PermitTemplate =
        templates[type] ?: templates.getValue(PermitType.GENERAL_WORK)

    fun getAllTemplates(): List<PermitTemplate> = templates.values.toList()
}
