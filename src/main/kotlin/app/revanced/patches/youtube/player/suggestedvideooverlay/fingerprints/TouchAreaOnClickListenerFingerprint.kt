package app.revanced.patches.youtube.player.suggestedvideooverlay.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object TouchAreaOnClickListenerFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("Landroid/view/View$OnClickListener;"),
    opcodes = listOf(
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.IF_EQZ,
        Opcode.CONST_4,
        Opcode.GOTO,
        Opcode.CONST_4,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.RETURN_VOID
    ),
    customFingerprint = { methodDef, classDef -> methodDef.name == "b" }
)
