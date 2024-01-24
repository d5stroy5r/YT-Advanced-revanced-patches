package app.revanced.patches.youtube.player.suggestedvideooverlay

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.youtube.player.suggestedvideooverlay.fingerprints.CoreContainerBuilderFingerprint
import app.revanced.patches.youtube.player.suggestedvideooverlay.fingerprints.TouchAreaOnClickListenerFingerprint
import app.revanced.patches.youtube.utils.integrations.Constants.PLAYER
import app.revanced.patches.youtube.utils.resourceid.SharedResourceIdPatch
import app.revanced.patches.youtube.utils.resourceid.SharedResourceIdPatch.CoreContainer
import app.revanced.patches.youtube.utils.settings.SettingsPatch
import app.revanced.util.exception
import app.revanced.util.getWideLiteralInstructionIndex
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
@Patch(
    name = "Hide suggested video overlay",
    description = "Hide the suggested video overlay to play next.",
    dependencies = [
        SettingsPatch::class,
        SharedResourceIdPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.youtube",
            [
                "18.25.40",
                "18.27.36",
                "18.29.38",
                "18.30.37",
                "18.31.40",
                "18.32.39",
                "18.33.40",
                "18.34.38",
                "18.35.36",
                "18.36.39",
                "18.37.36",
                "18.38.44",
                "18.39.41",
                "18.40.34",
                "18.41.39",
                "18.42.41",
                "18.43.45",
                "18.44.41",
                "18.45.43"
            ]
        )
    ]
)
@Suppress("unused")
object SuggestedVideoOverlayPatch : BytecodePatch(
    setOf(
        CoreContainerBuilderFingerprint,
        TouchAreaOnClickListenerFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        CoreContainerBuilderFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetIndex = getWideLiteralInstructionIndex(CoreContainer) + 4
                val targetReference =
                    getInstruction<ReferenceInstruction>(targetIndex).reference

                if (!targetReference.toString().endsWith("Landroid/view/ViewGroup;"))
                    throw PatchException("Reference did not match: $targetReference")

                val targetRegister =
                    getInstruction<TwoRegisterInstruction>(targetIndex).registerA

                addInstruction(
                    targetIndex,
                    "invoke-static {v$targetRegister}, $PLAYER->hideSuggestedVideoOverlay(Landroid/view/ViewGroup;)V"
                )
            }
        } ?: throw CoreContainerBuilderFingerprint.exception

        TouchAreaOnClickListenerFingerprint.result?.let {
            it.mutableMethod.apply {
                val targetindex = it.scanResult.patternScanResult!!.startIndex + 1
                val targetReference =
                    getInstruction<ReferenceInstruction>(targetIndex).reference.toString()
                val targetRegister = getInstruction<Instruction35c>(targetindex).registerC
                
                if (targetReference != "Landroid/view/View;->setOnClickListener(Landroid/view/View\$OnClickListener;)V")
                    throw TouchAreaOnClickListenerFingerprint.exception

                addInstruction(
                    targetindex + 1,
                    "invoke-static {v$targetRegister}, $PLAYER->hideSuggestedVideoOverlay(Landroid/view/View;)V"
                )
            }
        } ?: throw TouchAreaOnClickListenerFingerprint.exception

        /**
         * Add settings
         */
        SettingsPatch.addPreference(
            arrayOf(
                "PREFERENCE: PLAYER_SETTINGS",
                "SETTINGS: PLAYER_EXPERIMENTAL_FLAGS",
                "SETTINGS: HIDE_SUGGESTED_VIDEO_OVERLAY"
            )
        )

        SettingsPatch.updatePatchStatus("Hide suggested video overlay")

    }
}
