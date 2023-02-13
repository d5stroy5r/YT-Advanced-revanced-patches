package app.revanced.patches.music.audio.exclusiveaudio.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.music.audio.exclusiveaudio.fingerprints.AudioOnlyEnablerFingerprint
import app.revanced.shared.annotation.YouTubeMusicCompatibility
import app.revanced.shared.extensions.toErrorResult

@Patch
@Name("exclusive-audio-playback")
@Description("Enables the option to play music without video.")
@YouTubeMusicCompatibility
@Version("0.0.1")
class ExclusiveAudioPatch : BytecodePatch(
    listOf(
        AudioOnlyEnablerFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        AudioOnlyEnablerFingerprint.result?.mutableMethod?.let {
            it.replaceInstruction(it.implementation!!.instructions.count() - 1, "const/4 v0, 0x1")
            it.addInstruction("return v0")
        } ?: return AudioOnlyEnablerFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}
